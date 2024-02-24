package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.api.controller.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.utils.DateUtil;
import com.swifty.bank.server.core.common.utils.JwtUtil;
import com.swifty.bank.server.core.common.utils.RedisUtil;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.exception.NoSuchAuthByUuidException;
import com.swifty.bank.server.exception.NotLoggedInCustomerException;
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
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RedisUtil redisUtil;
    private final AuthRepository authRepository;

    @Value("${jwt.access-token-expiration-millis}")
    private int accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration-millis}")
    private int refreshTokenExpiration;

    @Override
    public TokenDto generateTokenDtoWithCustomer(Customer customer) {
        return new TokenDto(createAccessToken(customer), createRefreshToken(customer));
    }

    @Override
    public void logout(UUID uuid) {
        if (!isLoggedOut(uuid)) {
            String key = uuid.toString();
            Auth prevAuth = redisUtil.getRedisAuthValue(key);
            if (prevAuth == null) {
                prevAuth = authRepository.findAuthByUuid(uuid)
                        .orElseThrow(() -> new NoSuchAuthByUuidException("[ERROR] 해당 유저의 로그인 정보가 없습니다."));
            }

            prevAuth.updateAuthContent("LOGOUT");
            redisUtil.saveAuthRedis(key, prevAuth);
        }
        throw new NotLoggedInCustomerException("[ERROR] 로그인 되지 않은 유저가 로그 아웃을 시도했습니다.");
    }

    @Override
    public boolean isLoggedOut(UUID uuid) {
        Auth res = redisUtil.getRedisAuthValue(uuid.toString());
        if (res == null) {
            res = findAuthByUuid(uuid)
                    .orElseThrow(() -> new NoSuchAuthByUuidException("[ERROR] 해당 유저의 로그인 정보가 없습니다."));
            redisUtil.saveAuthRedis(uuid.toString(), res);
        }
        return res.getRefreshToken().equals("LOGOUT");
    }

    @Override
    public Optional<Auth> findAuthByUuid(UUID uuid) {
        return authRepository.findAuthByUuid(uuid);
    }

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

        claims.setSubject("RefreshToken");
        claims.put("customerId", customer.getId());
        return JwtUtil.generateToken(claims, expiration);
    }

    public UUID extractCustomerId(String jwt) {
        return UUID.fromString(JwtUtil.getClaimByKey(jwt, "customerId").toString());
    }

    @Override
    @Transactional
    public void saveRefreshTokenInDataSources(String token) {
        UUID uuid = UUID.fromString(JwtUtil.getClaimByKey(token, "customerId").toString());

        Auth previousAuth = redisUtil.getRedisAuthValue(uuid.toString());
        if (previousAuth == null) {
            previousAuth = findAuthByUuid(uuid)
                    .orElse(null);
        }
        Auth newAuth;

        if (previousAuth != null) {
            previousAuth.updateAuthContent(token);
            newAuth = previousAuth;
        } else {
            newAuth = new Auth(uuid, token);
            authRepository.save(newAuth);
        }
        redisUtil.saveAuthRedis(uuid.toString(), newAuth);
    }
}