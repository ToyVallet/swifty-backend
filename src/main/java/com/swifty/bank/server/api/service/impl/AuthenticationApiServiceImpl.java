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
import com.swifty.bank.server.utils.JwtUtil;
import com.swifty.bank.server.utils.RedisUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        Customer customerByDeviceId = customerService.findByDeviceId(dto.getDeviceId());
        Customer customer = customerService.join(dto);
        if (customerByDeviceId != null) {
            customerService.updateDeviceId(
                    customerByDeviceId.getId(),
                    null
            );
        }

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

        Customer customer = customerService.findByDeviceId(deviceId);
        if (customer == null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] there is no device logged in with device " + deviceId,
                    null
            );
        }

        if (uuid.toString().equals(customer.getId().toString())
                && customer.getDeviceId().equals(deviceId)) {
            return this.storeRefreshToken(customer);
        }

        return new ResponseResult(Result.FAIL,
                "[ERROR] Latest user of device is not match with token. It might be hijacked",
                null
        );
    }

    @Override
    public ResponseResult<?> loginWithForm(String deviceId, String phoneNumber) {
        Customer customerByDeviceID = customerService.findByDeviceId(deviceId);
        Customer customerByPhoneNumber = customerService.findByPhoneNumber(phoneNumber);

        if (customerByPhoneNumber == null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] No registered user with phone number, cannot login",
                    null
            );
        }

        if (customerByDeviceID == null) {
            customerByPhoneNumber = customerService.updateDeviceId(
                    customerByPhoneNumber.getId(),
                    deviceId
            );
        } else {
            if (!customerByDeviceID.getId().equals(customerByPhoneNumber.getId())) {
                customerService.updateDeviceId(
                        customerByPhoneNumber.getId(),
                        deviceId
                );
                customerService.updateDeviceId(
                        customerByDeviceID.getId(),
                        null
                );
            }
        }

        return this.storeRefreshToken(customerByPhoneNumber);
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

        Customer customer = customerService.findByUuid(uuid);
        if (customer == null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] No Such Customer with the uuid",
                    null
            );
        }
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

        logout(token);
        Customer customer = customerService.withdrawCustomer(uuid);
        if (customer == null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] there is no the customer containing that information",
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] " + uuid + " successfully withdraw",
                null
        );
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
