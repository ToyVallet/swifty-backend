package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.exception.NoSuchAuthByUuidException;
import com.swifty.bank.server.core.common.authentication.exception.NotLoggedInCustomerException;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.utils.DateUtil;
import com.swifty.bank.server.utils.JwtUtil;
import com.swifty.bank.server.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;
    private final AuthRepository authRepository;

    @Value("${jwt.access-token-expiration-millis}")
    private int accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration-millis}")
    private int refreshTokenExpiration;

    @Override
    public TokenDto generateTokenDtoWithCustomer(Customer customer) {
        TokenDto tokens = new TokenDto(createAccessToken(customer), createRefreshToken(customer));
        return tokens;
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

    private String createAccessToken(Customer customer) {
        Claims claims = Jwts.claims();
        Date now = DateUtil.now();

        claims.put("id", customer.getId());
        claims.put("scopes", List.of(new SimpleGrantedAuthority("CUSTOMER")));
        claims.setSubject("ACCESS");
        claims.setExpiration(DateUtil.millisToDate(now.getTime() + accessTokenExpiration * 1000L));
        return jwtUtil.generateToken(claims);
    }

    private String createRefreshToken(Customer customer) {
        Claims claims = Jwts.claims();
        Date now = DateUtil.now();

        claims.put("id", customer.getId());
        claims.put("scopes", List.of(new SimpleGrantedAuthority("CUSTOMER")));
        claims.setSubject("REFRESH");
        claims.setExpiration(DateUtil.millisToDate(now.getTime() + refreshTokenExpiration * 1000L));
        return jwtUtil.generateToken(claims);
    }

    @Override
    @Transactional
    public void saveRefreshTokenInDataSources(String token) {
        UUID uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", token).toString());

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