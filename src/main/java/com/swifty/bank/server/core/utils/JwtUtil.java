package com.swifty.bank.server.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtUtil {
    private static String secretKey;

    public static String removeType(String jwt) {
        // "Bearer ey..." 꼴인 경우 "Bearer " 제거
        return jwt.replaceFirst("Bearer ", "");
    }

    public static String generateToken(Claims claims, Date expiration) {
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    public static String getSubject(String jwt) {
        validateToken(jwt);
        Claims claims = getAllClaims(jwt);
        return claims.getSubject();
    }

    public static Object getClaimByKey(String jwt, String key) {
        validateToken(jwt);

        Claims claims = getAllClaims(jwt);
        claims.getExpiration();
        // validate key
        if (!claims.containsKey(key)) {
            throw new IllegalArgumentException("[ERROR] 토큰에 '" + key + "'로 설정된 key가 없습니다");
        }
        return claims.get(key);
    }

    private static Claims getAllClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /*
     * validate
     * 1. JWT was incorrectly constructed
     * 2. JWS signature was discovered, but could not be verified
     * 3. expired token
     */
    public static void validateToken(String jwt) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build();
        try {
            jwtParser.parse(jwt);
        } catch (Exception e) {
            throw new IllegalArgumentException("올바른 jwt가 아닙니다.");
        }
    }


    public static boolean isExpiredToken(String jwt) {
        Key secretKey = getSecretKey();

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
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
