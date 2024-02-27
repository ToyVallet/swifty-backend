package com.swifty.bank.server.core.utils;

import com.swifty.bank.server.exception.TokenContentNotValidException;
import com.swifty.bank.server.exception.TokenExpiredException;
import com.swifty.bank.server.exception.TokenNotExistException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
public class JwtUtil {
    private static String secretKey;

    public static String generateToken(Claims claims, Date expiration) {
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    public static String extractJwtFromCurrentRequestHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        try {
            String token = request.getHeader("Authorization").split("Bearer ")[1];
            validateToken(token);
            return token;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바른 jwt가 존재하지 않습니다.");
        }
    }

    public static Object getClaimByKey(String token, String key) {
        validateToken(token);

        Claims claims = getAllClaims(token);
        // validate key
        if (!claims.containsKey(key)) {
            throw new IllegalArgumentException("[ERROR] 토큰에 '" + key + "'로 설정된 key가 없습니다");
        }
        return claims.get(key);
    }

    private static Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /*
     * validate
     * 1. JWT was incorrectly constructed
     * 2. JWS signature was discovered, but could not be verified
     * 3. expired token
     */
    public static void validateToken(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build();
        try {
            jwtParser.parse(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("올바른 jwt가 아닙니다.");
        }
    }


    public static boolean isExpiredToken(String accessToken) {
        Key secretKey = getSecretKey();

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    private static Key getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Value("${jwt.secret}")
    public void setSecretKey(String secret) {
        JwtUtil.secretKey = secret;
    }

    public static <T> T getValueByKeyWithObject(String jwt, String key, Class<T> objectClass) {
        if (objectClass == UUID.class) {
            return objectClass.cast(UUID.fromString(JwtUtil.getClaimByKey(jwt, key).toString()));
        }
        throw new IllegalArgumentException("[ERROR] jwt에서 key를 추출하는 데 지원하지 않는 클래스 양식입니다");
    }
}
