package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.JoinRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.common.authentication.RefreshToken;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.TemporarySignUpFormRedisService;
import com.swifty.bank.server.core.common.redis.service.impl.OtpRedisServiceImpl;
import com.swifty.bank.server.core.common.redis.service.impl.RefreshTokenRedisServiceImpl;
import com.swifty.bank.server.core.common.redis.value.RefreshTokenCache;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.domain.sms.service.VerifyService;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.exception.authentication.NoSuchAuthByUuidException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationApiServiceImpl implements AuthenticationApiService {
    private final CustomerService customerService;
    private final AuthenticationService authenticationService;
    private final VerifyService verifyService;

    private final TemporarySignUpFormRedisService temporarySignUpFormRedisService;
    private final OtpRedisServiceImpl otpRedisService;
    private final RefreshTokenRedisServiceImpl refreshTokenRedisService;

    @Override
    public CheckLoginAvailabilityResponse checkLoginAvailability(
            CheckLoginAvailabilityRequest checkLoginAvailabilityRequest) {
        String phoneNumber = checkLoginAvailabilityRequest.getPhoneNumber();

        Optional<Customer> maybeCustomer = customerService.findByPhoneNumber(phoneNumber);
        // 기존에 가입된 번호인 경우, db 내용과 요청 폼이 일치하는지 확인
        // 현재 프론트에서 입력받는 데이터(성명, 주민등록번호, 통신사, 휴대폰 번호)와 백엔드에서 저장하는 고객의 데이터(이름, 국적, 성별, 생일 등등)가 상이하므로 일단 일부(이름, 휴대폰 번호)만 비교하도록 처리했음
        if (maybeCustomer.isPresent()) {
            Customer customer = maybeCustomer.get();

            // 이름과 휴대폰 번호가 같지 않으면 회원가입/로그인 진행 불가
            if (!(checkLoginAvailabilityRequest.getName().equals(customer.getName())
                    && checkLoginAvailabilityRequest.getPhoneNumber().equals(customer.getPhoneNumber()))) {
                return CheckLoginAvailabilityResponse.builder()
                        .isAvailable(false)
                        .temporaryToken("")
                        .build();
            }
        }

        String temporaryToken = authenticationService.createTemporaryToken();
        temporarySignUpFormRedisService.setData(
                temporaryToken,
                TemporarySignUpForm.builder()
                        .name(checkLoginAvailabilityRequest.getName())
                        .residentRegistrationNumber(checkLoginAvailabilityRequest.getResidentRegistrationNumber())
                        .MobileCarrier(checkLoginAvailabilityRequest.getMobileCarrier())
                        .phoneNumber(checkLoginAvailabilityRequest.getPhoneNumber())
                        .build()
        );
        return CheckLoginAvailabilityResponse.builder()
                .isAvailable(true)
                .temporaryToken(temporaryToken)
                .build();
    }

    @Override
    public ResponseResult<?> join(JoinRequest dto) {
        if (customerService.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "이미 가입된 번호입니다.",
                    null
            );
        }

        if (!verifyService.isVerified(dto.getPhoneNumber())) {
            // 만료 되어서 사라졌거나 인증이 된 상태가 아닌 경우
            return new ResponseResult<>(
                    Result.FAIL,
                    "인증이 완료되지 않은 번호입니다.",
                    null
            );
        }

        Optional<Customer> mayBeCustomerByDeviceId = customerService.findByDeviceId(dto.getDeviceId());
        if (mayBeCustomerByDeviceId.isPresent()) {
            Customer customer = mayBeCustomerByDeviceId.get();
            customerService.updateDeviceId(customer.getId(), null);
        }

        customerService.join(JoinDto.createJoinDto(dto));
        // 회원가입 절차가 완료된 경우, 전화번호 인증 여부 redis에서 삭제
        otpRedisService.deleteData(dto.getPhoneNumber());

        return new ResponseResult<>(Result.SUCCESS, "사용자가 성공적으로 등록되었습니다.", null);
    }

    @Transactional
    @Override
    public ResponseResult<?> loginWithForm(String deviceId, String phoneNumber) {
        Optional<Customer> mayBeCustomerByPhoneNumber = customerService.findByPhoneNumber(phoneNumber);
        if (mayBeCustomerByPhoneNumber.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "해당 번호로 가입된 사용자가 존재하지 않습니다.",
                    null
            );
        }
        Customer customerByPhoneNumber = mayBeCustomerByPhoneNumber.get();

        Optional<Customer> mayBeCustomerByDeviceId = customerService.findByDeviceId(deviceId);

        if (mayBeCustomerByDeviceId.isPresent()) {
            Customer customerByDeviceId = mayBeCustomerByDeviceId.get();

            customerService.updateDeviceId(customerByDeviceId.getId(), null);
            try {
                if (!authenticationService.isLoggedOut(customerByDeviceId.getId())) {
                    authenticationService.logout(customerByDeviceId.getId());
                }
            } catch (NoSuchAuthByUuidException e) {
                // 로그인 안 됐을때는 패스
            }
            customerService.updateDeviceId(customerByPhoneNumber.getId(), deviceId);
        }

        Map<String, Object> result = authenticationService.generateAndStoreRefreshToken(customerByPhoneNumber);
        if (result == null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "refresh token 생성 및 저장에 실패했습니다.",
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "로그인 인증에 성공하였습니다.",
                result
        );
    }

    @Override
    public ResponseResult<?> reissue(String jwt) {
        Map<String, Object> result = new HashMap<>();
        UUID customerId = JwtUtil.getValueByKeyWithObject(jwt, "customerId", UUID.class);

        // 로그아웃 된 유저가 아니어야 함
        if (authenticationService.isLoggedOut(customerId)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "로그아웃된 유저입니다.",
                    null
            );
        }
        // 이전 DB에 저장된 Ref. 토큰과 같은 값인지 비교
        if (!isValidatedRefreshToken(jwt)) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "현재 유효하지 않은 refresh token입니다.",
                    null
            );
        }

        Optional<Customer> mayBeCustomer = customerService.findByUuid(customerId);
        if (mayBeCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "유효하지 않은 uuid입니다.",
                    null
            );
        }

        Customer customer = mayBeCustomer.get();
        result = authenticationService.generateAndStoreRefreshToken(customer);
        if (result == null) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "refresh token 생성 및 저장에 실패했습니다.",
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "refresh token 재발급이 성공하였습니다.",
                result
        );
    }

    @Override
    public ResponseResult<?> logout(String jwt) {
        UUID customerId = JwtUtil.getValueByKeyWithObject(jwt, "customerId", UUID.class);
        authenticationService.logout(customerId);
        return new ResponseResult<>(Result.SUCCESS, "[INFO] user " + customerId + " logged out", null);
    }

    @Override
    public ResponseResult<?> signOut(String jwt) {
        UUID uuid = JwtUtil.getValueByKeyWithObject(jwt, "customerId", UUID.class);
        authenticationService.logout(uuid);
        customerService.withdrawCustomer(uuid);

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] " + uuid + " successfully withdraw",
                null
        );
    }

    private boolean isValidatedRefreshToken(String jwt) {
        UUID uuid = JwtUtil.getValueByKeyWithObject(jwt, "customerId", UUID.class);

        // get refresh token from redis
        RefreshTokenCache previousCache = refreshTokenRedisService.getData(uuid.toString());
        String prevRefToken = null;

        if (previousCache == null) {
            // get refresh token from mysql
            RefreshToken previousRefreshToken = authenticationService.findAuthByCustomerId(uuid)
                    .orElse(null);

            // 마지막으로 저장된 ref. 토큰과 현재 토큰이 맞지 않다면 유효하지 않은 토큰임
            if (previousRefreshToken == null) {
                return false;
            }

            prevRefToken = previousRefreshToken.getRefreshToken();
        } else {
            prevRefToken = previousCache.getRefreshToken();
        }

        return jwt.equals(prevRefToken);
    }
}
