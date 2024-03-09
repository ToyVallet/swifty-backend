package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.utils.DateUtil;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
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

        claims.setSubject("Auth");
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
    @Transactional(readOnly = false)
    public Auth saveRefreshTokenInDatabase(String refreshToken) {
        UUID customerUuid = UUID.fromString(JwtUtil.getClaimByKey(refreshToken, "customerUuid").toString());

        Optional<Auth> maybeAuth = authRepository.findAuthByUuid(customerUuid);

        if (maybeAuth.isPresent()) {
            Auth auth = maybeAuth.get();
            auth.updateRefreshToken(refreshToken);
            return auth;
        }
        return authRepository.save(new Auth(customerUuid, refreshToken));
    }
}