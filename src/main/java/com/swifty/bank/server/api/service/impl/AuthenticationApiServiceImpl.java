package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.ReissueResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignWithFormResponse;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenService;
import com.swifty.bank.server.core.common.redis.service.TemporarySignUpFormRedisService;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationApiServiceImpl implements AuthenticationApiService {
    private final CustomerService customerService;
    private final AuthenticationService authenticationService;

    private final TemporarySignUpFormRedisService temporarySignUpFormRedisService;
    private final LogoutAccessTokenService logoutAccessTokenService;

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
                        .mobileCarrier(checkLoginAvailabilityRequest.getMobileCarrier())
                        .phoneNumber(checkLoginAvailabilityRequest.getPhoneNumber())
                        .build()
        );
        return CheckLoginAvailabilityResponse.builder()
                .isAvailable(true)
                .temporaryToken(temporaryToken)
                .build();
    }

    @Override
    public SignWithFormResponse signUpAndSignIn(String temporaryToken, SignWithFormRequest signWithFormRequest) {
        TemporarySignUpForm temporarySignUpForm = temporarySignUpFormRedisService.getData(temporaryToken);

        Optional<Customer> mayBeCustomerByPhoneNumber
                = customerService.findByPhoneNumber(temporarySignUpForm.getPhoneNumber());

        if (mayBeCustomerByPhoneNumber.isPresent()) {
            Customer customer = mayBeCustomerByPhoneNumber.get();

            // 기존 회원이면서 form과 정보가 일치하는 경우
            if (customer.getName().equals(temporarySignUpForm.getName())) {
                TokenDto tokenDto = authenticationService.generateTokenDto(customer.getId());
                authenticationService.saveRefreshTokenInDatabase(tokenDto.getRefreshToken());
                temporarySignUpFormRedisService.deleteData(temporaryToken);

                return SignWithFormResponse.builder()
                        .isSuccess(true)
                        .tokens(List.of(tokenDto.getAccessToken(), tokenDto.getRefreshToken()))
                        .build();
            }
        }

        // 신규 회원인 경우
        if (mayBeCustomerByPhoneNumber.isEmpty()) {
            JoinDto joinDto = JoinDto.builder()
                    .name(temporarySignUpForm.getName())
                    .phoneNumber(temporarySignUpForm.getPhoneNumber())
                    .birthDate(temporarySignUpForm.getResidentRegistrationNumber())
                    .password(signWithFormRequest.getPassword())
                    .deviceId(signWithFormRequest.getDeviceId())
                    .build();

            Customer customer = customerService.join(joinDto);
            TokenDto tokenDto = authenticationService.generateTokenDto(customer.getId());
            authenticationService.saveRefreshTokenInDatabase(tokenDto.getRefreshToken());
            temporarySignUpFormRedisService.deleteData(temporaryToken);

            return SignWithFormResponse.builder()
                    .isSuccess(true)
                    .tokens(List.of(tokenDto.getAccessToken(), tokenDto.getRefreshToken()))
                    .build();
        }

        return SignWithFormResponse.builder()
                .isSuccess(false)
                .tokens(null)
                .build();
    }

    @Override
    public ReissueResponse reissue(String refreshToken) {
        if (!isValidatedRefreshToken(refreshToken)) {
            return ReissueResponse.builder()
                    .isSuccess(false)
                    .tokens(null)
                    .build();
        }
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(refreshToken, "customerUuid", UUID.class);
        TokenDto tokenDto = authenticationService.generateTokenDto(customerUuid);
        return ReissueResponse.builder()
                .isSuccess(true)
                .tokens(List.of(tokenDto.getAccessToken(), tokenDto.getRefreshToken()))
                .build();
    }

    @Override
    public ResponseResult<?> logout(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        authenticationService.deleteAuth(customerUuid);

        logoutAccessTokenService.setDataIfAbsent(accessToken, "false");
        return new ResponseResult<>(Result.SUCCESS, "[INFO] user " + customerUuid + " logged out", null);
    }

    @Override
    public ResponseResult<?> signOut(String jwt) {
        UUID uuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);
        authenticationService.deleteAuth(uuid);
        customerService.withdrawCustomer(uuid);

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] " + uuid + " successfully withdraw",
                null
        );
    }

    /*
     * 검증 1. jwt 자체 유효성 검증(만료기간, 시그니처)
     * 검증 2. refreshToken 안에 customerUuid 값이 포함되어 있는가?
     * 검증 3. DB에 저장되어 있는 refresh token과 값이 일치하는가?
     */
    private boolean isValidatedRefreshToken(String refreshToken) {
        JwtUtil.validateToken(refreshToken);

        UUID customerUuid = JwtUtil.getValueByKeyWithObject(refreshToken, "customerUuid", UUID.class);
        Optional<Auth> maybeAuth = authenticationService.findAuthByCustomerUuid(customerUuid);
        if (maybeAuth.isEmpty()) {
            return false;
        }

        Auth auth = maybeAuth.get();
        return refreshToken.equals(auth.getRefreshToken());
    }
}