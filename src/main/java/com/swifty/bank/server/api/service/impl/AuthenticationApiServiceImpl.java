package com.swifty.bank.server.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.exception.AuthenticationException;
import com.swifty.bank.server.core.common.authentication.exception.StoredAuthValueNotExistException;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.utils.HashUtil;
import com.swifty.bank.server.utils.JwtUtil;
import com.swifty.bank.server.utils.RedisUtil;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationApiServiceImpl implements AuthenticationApiService {
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;
    private final RedisUtil redisUtil;

    @Override
    public ResponseResult<?> join(JoinRequest dto) {
        if (customerService.findByPhoneNumber(dto.getPhoneNumber()) != null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Customer retrieval is not valid",
                    null
            );
        }

        String phoneVerified = redisUtil.getRedisStringValue(
                HashUtil.createStringHash(
                        List.of(dto.getDeviceId(),
                                dto.getPhoneNumber()))
        );
        if (phoneVerified == null || !phoneVerified.equals("true")) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] not verified phone number",
                    null
            );
        }

        Optional<Customer> mayBeCustomerByDeviceId = customerService.findByDeviceId(dto.getDeviceId());
        if (mayBeCustomerByDeviceId.isPresent()) {
            Customer customerByDeviceId = mayBeCustomerByDeviceId.get();
            customerService.updateDeviceId(customerByDeviceId.getId(),null);
        }

        Customer customer = customerService.join(dto);

        return this.storeRefreshToken(customer);
    }

    @Override
    public ResponseResult<?> loginWithJwt(String body, String token) {
        ObjectMapper mapper = new ObjectMapper();
        String deviceId;
        try {
            Map<String, String> map = mapper.readValue(body, Map.class);
            deviceId = map.get("deviceId");
        } catch (JsonProcessingException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Json format is not valid",
                    null
            );
        }

        if (deviceId == null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Device ID not exist",
                    null
            );
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", token).toString());
        } catch (AuthenticationException e) {
            return new ResponseResult(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        Optional<Customer> mayBeCustomerByDevice = customerService.findByDeviceId(deviceId);
        if (mayBeCustomerByDevice.isEmpty()) return new ResponseResult<>(
                Result.FAIL,
                "[ERROR] there is no device logged in with device " + deviceId,
                null
        );


        Customer customer = mayBeCustomerByDevice.get();

        if (uuid.toString().equals(customer.getId())
                && customer.getDeviceId().equals(deviceId)) {
            return storeRefreshToken(customer);
        }

        return new ResponseResult(Result.FAIL,
                "[ERROR] Latest user of device is not match with token. It might be hijacked",
                null
        );
    }

    @Transactional
    @Override
    public ResponseResult<?> loginWithForm(String deviceId, String phoneNumber) {
        Optional<Customer> mayBeCustomerByPhoneNumber = customerService.findByPhoneNumber(phoneNumber);
        if (mayBeCustomerByPhoneNumber.isEmpty()) return new ResponseResult<>(
                Result.FAIL,
                "[ERROR] No registered user with phone number, cannot login",
                null
        );
        Customer customerByPhoneNumber = mayBeCustomerByPhoneNumber.get();

        Optional<Customer> mayBeCustomerByDeviceId = customerService.findByDeviceId(deviceId);

        if (mayBeCustomerByDeviceId.isPresent()) {
            Customer customerByDeviceId = mayBeCustomerByDeviceId.get();
            customerService.updateDeviceId(customerByDeviceId.getId(),null);
            customerService.updateDeviceId(customerByPhoneNumber.getId(),deviceId);
        }

        return storeRefreshToken(customerByPhoneNumber);
    }

    @Override
    public ResponseResult<?> reissue(String body) {
        ObjectMapper mapper = new ObjectMapper();
        UUID uuid;
        String refreshToken;
        try {
            Map<String, String> map = mapper.readValue(body, Map.class);
            refreshToken = map.get("RefreshToken");

            uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", refreshToken).toString());
        } catch (JsonProcessingException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Json format is not valid",
                    null
            );
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        if (isLoggedOut(uuid.toString())) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Logged out user tried reissue",
                    null
            );
        }
        if (redisUtil.getRedisStringValue(refreshToken) != null) {
            logout(refreshToken);

            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Already used fresh token",
                    null
            );
        }

        Optional<Customer> mayBeCustomer = customerService.findByUuid(uuid);
        if (mayBeCustomer.isEmpty()) return new ResponseResult<>(
                Result.FAIL,
                "[ERROR] No Such Customer with the uuid",
                null
        );

        Customer customer = mayBeCustomer.get();
        return this.storeRefreshToken(customer);
    }

    @Override
    public ResponseResult<?> logout(String token) {
        UUID uuid;
        try {
            uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", token).toString());
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        if (!isLoggedOut(uuid.toString())) {
            String key = uuid.toString();
            Auth prevAuth = redisUtil.getRedisAuthValue(key);
            Auth newAuth = new Auth("", true);

            redisUtil.setRedisStringValue(prevAuth.getRefreshToken(), key);
            redisUtil.saveAuthRedis(key, newAuth);

            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[INFO] " + uuid.toString() + "logged out successfully",
                    null
            );
        }
        return new ResponseResult<>(
                Result.FAIL,
                "[ERROR] " + uuid.toString() + "'s token information does not exist",
                null
        );
    }

    @Override
    public ResponseResult<?> signOut(String token) {
        UUID uuid;
        try {
            uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", token).toString());
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        try {
            customerService.withdrawCustomer(uuid);
            logout(token);
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[INFO] " + uuid + " successfully withdraw",
                    null
            );
        }catch (NoSuchElementException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] there is no the customer containing that information",
                    null
            );
        }
    }

    private ResponseResult<?> storeRefreshToken(Customer customer) {
        Map<String, Object> result = new HashMap<>();

        try {
            TokenDto tokens = authenticationService.generateTokenDtoWithCustomer(customer);
            result.put("token", tokens);
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[INFO] Authentication succeed with user id: " + customer.getId(),
                    result
            );
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }
    }

    private boolean isLoggedOut(String key) {
        Auth res = redisUtil.getRedisAuthValue(key);
        if (res == null) {
            throw new StoredAuthValueNotExistException("[ERROR] No value referred by those key");
        }
        return res.isLoggedOut();
    }
}
