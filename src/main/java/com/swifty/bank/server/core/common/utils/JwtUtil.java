package com.swifty.bank.server.core.common.utils;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
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

        return request.getHeader("Authorization").split("Bearer ")[1];
    }

    public static Object getClaimByKey(String token, String key) {
        if (token == null || token.isEmpty()) {
            throw new TokenNotExistException("[ERROR] there is no token");
        }
        // parsing
        if (token.startsWith("Bearer ")) {
            // IndexOutOfBound error expected
            token = token.split(" ")[1].trim();
        }
        // check if expired
        if (isExpiredToken(token)) {
            throw new TokenExpiredException("[ERROR] Token is expired, reissue it");
        }

        Claims claims = getAllClaims(token);
        // validate key
        if (!claims.containsKey(key)) {
            throw new TokenContentNotValidException("[ERROR] There is no '" + key + "' in token");
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
     * 2. JWS signature was discovered
     * 3. expired token
     */
    public static boolean validateToken(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build();
        try {
            jwtParser.parse(token);
            return true;
        } catch (Exception e) {
            return false;
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
}
