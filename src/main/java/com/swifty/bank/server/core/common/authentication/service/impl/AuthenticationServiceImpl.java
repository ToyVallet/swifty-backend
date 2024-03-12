package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.utils.DateUtil;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthRepository authRepository;

    @Value("${jwt.access-token-expiration-seconds}")
    private Long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration-seconds}")
    private Long refreshTokenExpiration;
    @Value("${jwt.temporary-token-expiration-seconds}")
    private Long temporaryTokenExpiration;

    @Override
    public String createAccessToken(UUID customerUuid) {
        Claims claims = Jwts.claims();
        Date expiration = DateUtil.millisToDate(DateUtil.now().getTime() + accessTokenExpiration * 1000L);

        claims.setSubject("AccessToken");
        claims.put("customerUuid", customerUuid);
        return JwtUtil.generateToken(claims, expiration);
    }

    @Override
    public String createRefreshToken(UUID customerUuid) {
        Claims claims = Jwts.claims();
        Date expiration = DateUtil.millisToDate(DateUtil.now().getTime() + refreshTokenExpiration * 1000L);

        claims.setSubject("RefreshToken");
        claims.put("customerUuid", customerUuid);
        return JwtUtil.generateToken(claims, expiration);
    }

    @Override
    public String createTemporaryToken() {
        Claims claims = Jwts.claims();
        Date expiration = DateUtil.millisToDate(DateUtil.now().getTime() + temporaryTokenExpiration * 1000L);

        claims.setSubject("TemporaryToken");
        return JwtUtil.generateToken(claims, expiration);
    }

    @Override
    public TokenDto generateTokenDto(UUID customerUuid) {
        return new TokenDto(createAccessToken(customerUuid), createRefreshToken(customerUuid));
    }

    @Override

    public void deleteAuth(UUID customerUuid) {
        Optional<Auth> maybeAuth = authRepository.findAuthByUuid(customerUuid);
        maybeAuth.ifPresent(authRepository::delete);
    }

    @Override
    public Optional<Auth> findAuthByCustomerUuid(UUID customerUuid) {
        return authRepository.findAuthByUuid(customerUuid);
    }

    @Override
    public Auth saveRefreshTokenInDatabase(String refreshToken) {
        UUID customerUuid = UUID.fromString(JwtUtil.getClaimByKey(refreshToken, "customerUuid").toString());

        Optional<Auth> maybeAuth = authRepository.findAuthByUuid(customerUuid);

        if (maybeAuth.isPresent()) {
            Auth auth = maybeAuth.get();
            auth.updateRefreshToken(refreshToken);
            authRepository.save(auth);
            return auth;
        }
        return authRepository.save(new Auth(customerUuid, refreshToken));
    }

    /*
     * 검증 1. jwt 자체 유효성 검증(만료기간, 시그니처)
     * 검증 2. claim 안에 customerUuid 값이 포함되어 있는가? subject가 "RefreshToken"인가?
     * 검증 3. DB에 저장되어 있는 refresh token과 값이 일치하는가?
     */
    @Override
    public boolean isValidateRefreshToken(String refreshToken) {
        JwtUtil.validateToken(refreshToken);

        UUID customerUuid = JwtUtil.getValueByKeyWithObject(refreshToken, "customerUuid", UUID.class);
        String sub = JwtUtil.getSubject(refreshToken);
        if (!sub.equals("RefreshToken")) {
            return false;
        }

        Optional<Auth> maybeAuth = findAuthByCustomerUuid(customerUuid);
        if (maybeAuth.isEmpty()) {
            return false;
        }

        Auth auth = maybeAuth.get();
        return refreshToken.equals(auth.getRefreshToken());
    }

    @Override
    public boolean isValidateSignUpPassword(String password, String residentRegistrationNumber, String phoneNumber) {
        if (password.length() != 6) return false;

        // 같은 문자가 3자리 이상 반복되는가?
        for (int index = 0; index < password.length() - 2; index++) {
            if (password.charAt(index) == password.charAt(index + 1)
                    && password.charAt(index + 1) == password.charAt(index + 2)) {
                return false;
            }
        }

        // 생년월일이 포함됐는가?
        String birthDate = residentRegistrationNumber.substring(0, 6);
        if (birthDate.equals(password)) {
            return false;
        }

        // 전화번호의 부분 문자열인가?
        if (phoneNumber.contains(password)) {
            return false;
        }

        return true;
    }
}