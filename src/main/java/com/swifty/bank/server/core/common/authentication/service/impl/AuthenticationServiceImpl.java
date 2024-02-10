package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.exception.TokenContentNotValidException;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.utils.DateUtil;
import com.swifty.bank.server.utils.JwtUtil;
import com.swifty.bank.server.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration-millis}")
    private int accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration-millis}")
    private int refreshTokenExpiration;

    @Override
    public TokenDto generateTokenDtoWithCustomer(Customer customer) {
        TokenDto tokens = new TokenDto(createAccessToken(customer), createRefreshToken(customer));
        saveRefreshTokenInRedis(tokens.getRefreshToken());
        return tokens;
    }

    private String createAccessToken(Customer customer) {
        Claims claims = Jwts.claims();
        Date now = DateUtil.now();

        claims.put("id", customer.getId());
        claims.put("scopes", List.of(new SimpleGrantedAuthority("CUSTOMER")));
        claims.setSubject("ACCESS");
        claims.setExpiration(new Date(now.getTime() + accessTokenExpiration * 1000L));
        return jwtUtil.generateToken(claims);
    }

    private String createRefreshToken(Customer customer) {
        Claims claims = Jwts.claims();
        Date now = DateUtil.now();

        claims.put("id", customer.getId());
        claims.put("scopes", List.of(new SimpleGrantedAuthority("CUSTOMER")));
        claims.setSubject("REFRESH");
        claims.setExpiration(new Date(now.getTime() + refreshTokenExpiration * 1000L));
        return jwtUtil.generateToken(claims);
    }

    private void saveRefreshTokenInRedis(String token) {
        UUID uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken("id", token).toString());
        Auth previousAuth = redisUtil.getRedisAuthValue(uuid.toString());
        Auth newAuth;

        if (previousAuth != null) {
            newAuth = new Auth(token, previousAuth.isLoggedOut());
            UUID prevUuid = UUID.fromString(
                    jwtUtil.getClaimByKeyFromToken("id", previousAuth.getRefreshToken()).toString());

            if (!uuid.toString().equals(prevUuid.toString())) {
                throw new TokenContentNotValidException("[ERROR] Two token's owner is different");
            }
        } else {
            newAuth = new Auth(token, false);
        }
        redisUtil.saveAuthRedis(uuid.toString(), newAuth);
    }
}