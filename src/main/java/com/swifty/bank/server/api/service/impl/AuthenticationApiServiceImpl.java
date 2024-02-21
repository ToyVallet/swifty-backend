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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        String isVerified = redisUtil.getRedisStringValue(
                HashUtil.createStringHash(List.of("otp-", dto.getPhoneNumber()))
        );
        if (isVerified == null || !isVerified.equals("true")) {
            // 만료 되어서 사라졌거나 인증이 된 상태가 아닌 경우
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] not verified phone number",
                    null
            );
        }

        Optional<Customer> mayBeCustomerByDeviceId = customerService.findByDeviceId(dto.getDeviceId());
        if (mayBeCustomerByDeviceId.isPresent()) {
            Customer customerByDeviceId = mayBeCustomerByDeviceId.get();
            customerService.updateDeviceId(customerByDeviceId.getId(), null);
        }

        Customer customer = customerService.join(dto);
        // 회원가입 절차가 완료된 경우, 전화번호 인증 여부 redis에서 삭제
        redisUtil.deleteRedisStringValue(HashUtil.createStringHash(List.of("otp-", dto.getPhoneNumber())));

        return this.storeAndGenerateRefreshToken(customer);
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

            customerService.updateDeviceId(customerByDeviceId.getId(), null);
            customerService.updateDeviceId(customerByPhoneNumber.getId(), deviceId);
        }

        return storeAndGenerateRefreshToken(customerByPhoneNumber);
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

        // 로그아웃 된 유저가 아니어야 함
        if (authenticationService.isLoggedOut(uuid)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Logged out user tried reissue",
                    null
            );
        }
        // 토큰 재사용 방지
        if (redisUtil.getRedisStringValue(refreshToken) != null) {
            authenticationService.logout(uuid);

            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Already used fresh token",
                    null
            );
        }
        // 이전 DB || Redis에 저장된 Ref. 토큰과 같은 값인지 비교
        if (!isValidatedRefreshToken(refreshToken)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 현재 유효하지 않은 리프레시 토큰입니다.",
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
        return this.storeAndGenerateRefreshToken(customer);
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

        try {
            authenticationService.logout(uuid);
            return new ResponseResult<>(Result.SUCCESS, "[INFO] user " + uuid.toString() + " logged out", null);
        } catch (StoredAuthValueNotExistException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] user's logged in information not exist",
                    null
            );
        }
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
        } catch (NoSuchElementException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] there is no the customer containing that information",
                    null
            );
        }
    }

    private ResponseResult<?> storeAndGenerateRefreshToken(Customer customer) {
        Map<String, Object> result = new HashMap<>();

        try {
            TokenDto tokens = authenticationService.generateTokenDtoWithCustomer(customer);
            authenticationService.saveRefreshTokenInDataSources(tokens.getRefreshToken());
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

    private boolean isValidatedRefreshToken(String token) {
        UUID uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", token).toString());

        Auth previousAuth = redisUtil.getRedisAuthValue(uuid.toString());
        if (previousAuth == null) {
            previousAuth = authenticationService.findAuthByUuid(uuid)
                    .orElse(null);
        }
        Auth newAuth;

        if (previousAuth != null) {
            // 마지막으로 저장된 ref. 토큰과 현재 토큰이 맞지 않다면 유효하지 않은 토큰임
            if (!token.equals(previousAuth.getRefreshToken())) {
                return false;
            }
        }
        return true;
    }
}
