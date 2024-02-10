package com.swifty.bank.server.utils;

import com.swifty.bank.server.core.common.authentication.exception.TokenContentNotValidException;
import com.swifty.bank.server.core.common.authentication.exception.TokenExpiredException;
import com.swifty.bank.server.core.common.authentication.exception.TokenNotExistException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class JwtUtil implements Serializable {
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)      // set claims in payload
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        if (expiration == null) {
            return false;
        }
        return expiration.before(DateUtil.now());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Object getClaimByKeyFromToken(String key, String token) {
        if (token == null || token.isEmpty()) {
            throw new TokenNotExistException("[ERROR] there is no token");
        }
        // parsing
        if (token.startsWith("Bearer ")) {
            // IndexOutOfBound error expected
            token = token.split(" ")[1].trim();
        }
        // check if expired
        if (!isTokenExpired(token)) {
            throw new TokenExpiredException("[ERROR] Token is expired, reissue it");
        }

        Claims claims = getAllClaimsFromToken(token);
        // validate key
        if (!claims.containsKey(key)) {
            throw new TokenContentNotValidException("[ERROR] There is no '" + key + "' in token");
        }
        return claims.get(key);
    }

    public String getSubjectFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }
}