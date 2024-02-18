package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.exception.AuthenticationException;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.exceptions.CannotReferCustomerByNullException;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByDeviceID;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByPhoneNumberException;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByUUID;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.utils.JwtTokenUtil;
import com.swifty.bank.server.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationApiServiceImpl implements AuthenticationApiService {
    private final CustomerService customerService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationService authenticationService;
    private final RedisUtil redisUtil;

    @Transactional
    @Override
    public ResponseResult<?> join(JoinRequest dto) {

        Optional<Customer> mayBeCustomerByPhoneNumber = customerService.findByPhoneNumber(dto.getPhoneNumber());
        if (mayBeCustomerByPhoneNumber.isPresent()) return new ResponseResult<>(
                Result.SUCCESS,
                "[ERROR] enrolled user with this phone number exists, cannot sign up",
                null
        );


        Optional<Customer> maybeCustomerByDeviceId = customerService.findByDeviceId(dto.getDeviceId());
        if (maybeCustomerByDeviceId.isPresent()) return new ResponseResult<>(
                Result.SUCCESS,
               "디바이스아이디가 중복됩니다.",
                null
        );

        Customer customer = customerService.join(dto);

        return new ResponseResult<>(
                Result.SUCCESS,
                "회원가입을 정상적으로 완료하였습니다.",
                customer
        );
    }

    @Override
    public ResponseResult<?> loginWithJwt(UUID uuid, String deviceId) {
        Optional<Customer> mayBeCustomerByDeviceId = customerService.findByDeviceId(deviceId);

        if (mayBeCustomerByDeviceId.isEmpty()) return new ResponseResult<>(
                Result.SUCCESS,
                "[ERROR] there is no device logged in with device " + deviceId,
                null
        );

        Optional<Customer> mayBeCustomer = customerService.findByUuid(uuid);
        if (mayBeCustomer.isEmpty()) return new ResponseResult(Result.FAIL,
                "회원이 존재하지 않습니다.",
                null
        );

        Customer customer = mayBeCustomer.get();

        if (customer.getDeviceId().equals(deviceId)) return storeRefreshToken(customer);

        return new ResponseResult(Result.FAIL,
                "[ERROR] Latest user of device is not match with token. It might be hijacked",
                null
        );
    }

    @Override
    public ResponseResult<?> loginWithForm(String deviceId, String phoneNumber) {
        Optional<Customer> mayBeCustomerByDeviceId = customerService.findByDeviceId(deviceId);
        if (mayBeCustomerByDeviceId.isEmpty()) return new ResponseResult<>(
                Result.SUCCESS,
                "deviceId와 일치하는 회원이 존재하지 않습니다.",
                null
        );

        Optional<Customer> mayBeCustomerByPhoneNumber = customerService.findByPhoneNumber(phoneNumber);
        if (mayBeCustomerByPhoneNumber.isEmpty()) return new ResponseResult<>(
                Result.SUCCESS,
                "[ERROR] No registered user with phone number, cannot login",
                null
        );

        Customer customer = mayBeCustomerByPhoneNumber.get();

        return storeRefreshToken(customer);
    }

    @Override
    public ResponseResult<?> reissue(UUID uuid, String refreshToken) {
        try {
            if (redisUtil.isLoggedOut(uuid.toString())) {
                return new ResponseResult<>(
                        Result.FAIL,
                        "[ERROR] Logged out user tried reissue",
                        null
                );
            }
            if (redisUtil.getRedisStringValue(refreshToken) != null) {
                logout(uuid);

                return new ResponseResult<>(
                        Result.FAIL,
                        "[ERROR] Already used fresh token",
                        null
                );
            }

            Customer customer = customerService.findByUuid(uuid).orElseThrow(() -> new CannotReferCustomerByNullException());
            return this.storeRefreshToken(customer);
        } catch (CannotReferCustomerByNullException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        } catch (NoSuchCustomerByUUID e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }
    }

    @Override
    public ResponseResult<?> logout(UUID uuid) {
        if (!redisUtil.isLoggedOut(uuid.toString())) {
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
    public ResponseResult<?> signOut(UUID uuid) {
        logout(uuid);
        try {
            customerService.withdrawCustomer(uuid);
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[INFO] " + uuid.toString() + " successfully withdraw",
                    null
            );
        } catch (NoSuchCustomerByUUID e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] " + uuid.toString() + " not exist",
                    null
            );
        }
    }

    private ResponseResult<?> storeRefreshToken(Customer customer) {
        Map<String, Object> result = new HashMap<>();

        try {
            TokenDto tokens = authenticationService.generateTokenWithCustomer(customer);
            result.put("token", tokens);
            return new ResponseResult<>(
                    Result.SUCCESS,
                    "[INFO] Authentication succeed with user id: " + customer.getId(),
                    result
            );
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Authentication failed building some tokens",
                    null
            );
        }
    }
}
