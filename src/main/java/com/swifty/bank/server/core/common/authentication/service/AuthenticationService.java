package com.swifty.bank.server.core.common.authentication.service;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthenticationService {
    TokenDto generateTokenDto(UUID customerUuid);

    void deleteAuth(UUID customerUuid);

    Optional<Auth> findAuthByCustomerUuid(UUID customerUuid);

    Auth saveRefreshTokenInDatabase(String token);

    String createAccessToken(UUID customerUuid);

    String createRefreshToken(UUID customerUuid);

    String createTemporaryToken();

    /*
     * 검증 1. jwt 자체 유효성 검증(만료기간, 시그니처)
     * 검증 2. claim 안에 customerUuid 값이 포함되어 있는가? subject가 "RefreshToken"인가?
     * 검증 3. DB에 저장되어 있는 refresh token과 값이 일치하는가?
     */
    boolean isValidateRefreshToken(String refreshToken);

    boolean isValidateSignUpPassword(String password, String residentRegistrationNumber, String phoneNumber);
}