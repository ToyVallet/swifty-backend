package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.LogoutResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.ReissueResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignOutResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignWithFormResponse;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.service.TemporarySignUpFormRedisService;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.core.utils.SBoxUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationApiServiceImpl implements AuthenticationApiService {
    private final static int PASSWORD_LEN = 6;

    private final CustomerService customerService;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder encoder;

    private final TemporarySignUpFormRedisService temporarySignUpFormRedisService;
    private final LogoutAccessTokenRedisService logoutAccessTokenRedisService;
    private final SBoxKeyRedisService sBoxKeyRedisService;

    @Override
    public CheckLoginAvailabilityResponse checkLoginAvailability(
            CheckLoginAvailabilityRequest checkLoginAvailabilityRequest) {
        String phoneNumber = checkLoginAvailabilityRequest.getPhoneNumber();

        Optional<Customer> maybeCustomer = customerService.findByPhoneNumber(phoneNumber);
        TemporarySignUpForm temporarySignUpForm = TemporarySignUpForm.builder()
                .name(checkLoginAvailabilityRequest.getName())
                .residentRegistrationNumber(checkLoginAvailabilityRequest.getResidentRegistrationNumber())
                .mobileCarrier(checkLoginAvailabilityRequest.getMobileCarrier())
                .phoneNumber(checkLoginAvailabilityRequest.getPhoneNumber())
                .build();

        // 기존에 가입된 번호인 경우, db 내용과 요청 폼이 일치하는지 확인
        if (maybeCustomer.isPresent()) {
            Customer customer = maybeCustomer.get();
            if (!customerService.isEqualCustomer(
                    customer,
                    temporarySignUpForm.getName(),
                    temporarySignUpForm.getResidentRegistrationNumber())
            ) {
                return CheckLoginAvailabilityResponse.builder()
                        .isAvailable(false)
                        .temporaryToken("")
                        .build();
            }
        }

        String temporaryToken = authenticationService.createTemporaryToken();
        temporarySignUpFormRedisService.setData(
                temporaryToken,
                temporarySignUpForm
        );

        return CheckLoginAvailabilityResponse.builder()
                .isAvailable(true)
                .temporaryToken(temporaryToken)
                .build();
    }

    @Override
    @Transactional
    public SignWithFormResponse signUpAndSignIn(String temporaryToken, SignWithFormRequest signWithFormRequest) {
        TemporarySignUpForm temporarySignUpForm = temporarySignUpFormRedisService.getData(temporaryToken);

        // 비밀번호 복호화
        List<Integer> key = sBoxKeyRedisService.getData(temporaryToken).getKey();
        List<Integer> decrypted = SBoxUtil.decrypt(signWithFormRequest.getPushedOrder(), key);
        String password = String.join("",
                decrypted
                        .stream()
                        .map(Object::toString)
                        .toList()
        );

        // 비밀번호 규칙 검증
        if (!authenticationService.isValidateSignUpPassword(password,
                temporarySignUpForm.getResidentRegistrationNumber(),
                temporarySignUpForm.getPhoneNumber())
        ) {
            return SignWithFormResponse.builder()
                    .isSuccess(false)
                    .isAvailablePassword(false)
                    .tokens(null)
                    .build();
        }

        Optional<Customer> mayBeCustomerByPhoneNumber
                = customerService.findByPhoneNumber(temporarySignUpForm.getPhoneNumber());

        if (mayBeCustomerByPhoneNumber.isPresent()) {
            Customer customer = mayBeCustomerByPhoneNumber.get();

            // 기존 회원이면서 이름, 성별, 생년월일이 일치하는가?
            if (customerService.isEqualCustomer(customer,
                    temporarySignUpForm.getName(),
                    temporarySignUpForm.getResidentRegistrationNumber())
            ) {
                TokenDto tokenDto = authenticationService.generateTokenDto(customer.getId());
                authenticationService.saveRefreshTokenInDatabase(tokenDto.getRefreshToken());
                temporarySignUpFormRedisService.deleteData(temporaryToken);

                // 기존 Customer의 비밀번호와 deviceId 업데이트
                customerService.updateDeviceId(customer.getId(), signWithFormRequest.getDeviceId());
                customerService.updatePassword(customer.getId(), password);

                return SignWithFormResponse.builder()
                        .isSuccess(true)
                        .isAvailablePassword(true)
                        .tokens(List.of(tokenDto.getAccessToken(), tokenDto.getRefreshToken()))
                        .build();
            }
        }

        // 신규 회원인 경우
        if (mayBeCustomerByPhoneNumber.isEmpty()) {
            JoinDto joinDto = JoinDto.builder()
                    .name(temporarySignUpForm.getName())
                    .nationality(Nationality.KOREA)
                    .phoneNumber(temporarySignUpForm.getPhoneNumber())
                    .password(password)
                    .deviceId(signWithFormRequest.getDeviceId())
                    .gender(customerService.extractGender(temporarySignUpForm.getResidentRegistrationNumber()))
                    .birthDate(customerService.extractBirthDate(temporarySignUpForm.getResidentRegistrationNumber()))
                    .build();

            // DB에 회원 추가
            Customer customer = customerService.join(joinDto);
            TokenDto tokenDto = authenticationService.generateTokenDto(customer.getId());
            authenticationService.saveRefreshTokenInDatabase(tokenDto.getRefreshToken());
            temporarySignUpFormRedisService.deleteData(temporaryToken);

            return SignWithFormResponse.builder()
                    .isSuccess(true)
                    .isAvailablePassword(true)
                    .tokens(List.of(tokenDto.getAccessToken(), tokenDto.getRefreshToken()))
                    .build();
        }

        return SignWithFormResponse.builder()
                .isSuccess(false)
                .isAvailablePassword(true)
                .tokens(null)
                .build();
    }

    @Override
    @Transactional
    public ReissueResponse reissue(String refreshToken) {
        if (!authenticationService.isValidateRefreshToken(refreshToken)) {
            return ReissueResponse.builder()
                    .isSuccess(false)
                    .tokens(null)
                    .build();
        }
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(refreshToken, "customerUuid", UUID.class);
        TokenDto tokenDto = authenticationService.generateTokenDto(customerUuid);
        authenticationService.saveRefreshTokenInDatabase(tokenDto.getRefreshToken());
        return ReissueResponse.builder()
                .isSuccess(true)
                .tokens(List.of(tokenDto.getAccessToken(), tokenDto.getRefreshToken()))
                .build();
    }

    @Override
    @Transactional
    public LogoutResponse logout(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        authenticationService.deleteAuth(customerUuid);

        logoutAccessTokenRedisService.setDataIfAbsent(accessToken, "false");
        return LogoutResponse.builder()
                .isSuccessful(true)
                .build();
    }

    @Override
    @Transactional
    public SignOutResponse signOut(String accessToken) {
        UUID uuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        authenticationService.deleteAuth(uuid);
        customerService.withdrawCustomer(uuid);
        logoutAccessTokenRedisService.setDataIfAbsent(accessToken, "false");
        return SignOutResponse.builder()
                .wasSignedOut(true)
                .build();
    }

    private Gender extractGender(String residentRegistrationNumber) {
        if (residentRegistrationNumber.endsWith("4") ||
                residentRegistrationNumber.endsWith("2")) {
            return Gender.FEMALE;
        }
        return Gender.MALE;
    }

    private String extractBirthDate(String residentRegistrationNumber) {
        return residentRegistrationNumber.substring(0, 6);
    }
}