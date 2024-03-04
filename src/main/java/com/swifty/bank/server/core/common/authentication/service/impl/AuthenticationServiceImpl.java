package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.RefreshTokenRedisService;
import com.swifty.bank.server.core.common.redis.value.RefreshTokenCache;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.utils.DateUtil;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.exception.authentication.AuthenticationException;
import com.swifty.bank.server.exception.authentication.NoSuchAuthByUuidException;
import com.swifty.bank.server.exception.authentication.NotLoggedInCustomerException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    private final RefreshTokenRedisService refreshTokenRedisService;

    @Value("${jwt.access-token-expiration-millis}")
    private int accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration-millis}")
    private int refreshTokenExpiration;
    @Value("${jwt.temporary-token-expiration-millis}")
    private int temporaryTokenExpiration;

    @Override
    public String createAccessToken(Customer customer) {
        Claims claims = Jwts.claims();
        Date expiration = DateUtil.millisToDate(DateUtil.now().getTime() + accessTokenExpiration * 1000L);

        claims.setSubject("AccessToken");
        claims.put("customerId", customer.getId());
        return JwtUtil.generateToken(claims, expiration);
    }

    @Override
    public String createRefreshToken(Customer customer) {
        Claims claims = Jwts.claims();
        Date expiration = DateUtil.millisToDate(DateUtil.now().getTime() + refreshTokenExpiration * 1000L);

        claims.setSubject("Auth");
        claims.put("customerId", customer.getId());
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
    public TokenDto generateTokenDto(Customer customer) {
        return new TokenDto(createAccessToken(customer), createRefreshToken(customer));
    }

    @Override
    public void logout(UUID customerId) {
        if (!isLoggedOut(customerId)) {
            String key = customerId.toString();
            Auth prevDbAuth = authRepository.findAuthByUuid(customerId)
                    .orElseThrow(() -> new NoSuchAuthByUuidException("[ERROR] 해당 유저의 로그인 정보가 없습니다."));

            prevDbAuth.updateRefreshToken("LOGOUT");
            refreshTokenRedisService.setData(key, new RefreshTokenCache("LOGOUT"));
            return;
        }
        throw new NotLoggedInCustomerException("[ERROR] 로그인 되지 않은 유저가 로그 아웃을 시도했습니다.");
    }

    @Override
    public boolean isLoggedOut(UUID customerId) {
        RefreshTokenCache refreshTokenCache = refreshTokenRedisService.getData(customerId.toString());

        if (refreshTokenCache == null) {
            Auth res = findAuthByCustomerId(customerId)
                    .orElseThrow(() -> new NoSuchAuthByUuidException("[ERROR] 해당 유저의 로그인 정보가 없습니다."));

            refreshTokenRedisService.setData(customerId.toString(),
                    new RefreshTokenCache(res.getRefreshToken()));
            return res.getRefreshToken().equals("LOGOUT");
        }

        return refreshTokenCache.getRefreshToken().equals("LOGOUT");
    }

    @Override
    public Map<String, Object> generateAndStoreRefreshToken(Customer customer) {
        Map<String, Object> result = new HashMap<>();

        try {
            TokenDto tokens = generateTokenDto(customer);
            saveRefreshTokenInDataSources(tokens.getRefreshToken());
            result.put("token", tokens);
            return result;
        } catch (AuthenticationException e) {
            return null;
        }
    }

    @Override
    public Optional<Auth> findAuthByCustomerId(UUID customerId) {
        return authRepository.findAuthByUuid(customerId);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveRefreshTokenInDataSources(String jwt) {
        UUID customerId = UUID.fromString(JwtUtil.getClaimByKey(jwt, "customerId").toString());

        Auth prevDbAuth = authRepository.findAuthByUuid(customerId)
                .orElse(null);

        if (prevDbAuth == null) {
            authRepository.save(new Auth(customerId, jwt));
        } else {
            prevDbAuth.updateRefreshToken(jwt);
        }

        refreshTokenRedisService.setData(customerId.toString(), new RefreshTokenCache(jwt));
    }
}