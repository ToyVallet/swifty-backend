package com.swifty.bank.server.core.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    private final long accessTokenValidTime = Duration.ofMinutes(30).toMillis(); // 만료시간 30분
    private final long refreshTokenValidTime = Duration.ofDays(14).toMillis(); // 만료시간 2주

    public String createJwtAccessToken(UUID customerUUID) {
        Key secretKey = getSecretKey();

        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .setSubject("AccessToken")
                .claim("customerId", customerUUID)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .signWith(secretKey)
                .compact();
    }

    public String createJwtRefreshToken(UUID customerUUID) {
        Key secretKey = getSecretKey();

        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .setSubject("RefreshToken")
                .claim("customerId", customerUUID)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidTime))
                .signWith(secretKey)
                .compact();
    }

    public String getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        return request.getHeader("Authorization").split("Bearer ")[1];
    }

    public UUID getCustomerId() {
        Key secretKey = getSecretKey();
        String accessToken = getAccessToken();

        if (!isValidateToken(accessToken)) {
            throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
        }

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken);

        return UUID.fromString(claimsJws.getBody().get("customerId", String.class));
    }


    public boolean isValidateToken(String token) {
        try {
            Key secretKey = getSecretKey();
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            String customerId = claimsJws.getBody().get("customerId", String.class);

            if (customerId.isEmpty()) {
                throw new IllegalArgumentException();
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public boolean isExpiredToken(String accessToken) {
        Key secretKey = getSecretKey();

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
