package com.swifty.bank.server.utils;

import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.exception.TokenContentNotValidException;
import com.swifty.bank.server.core.common.authentication.exception.TokenExpiredException;
import com.swifty.bank.server.core.common.authentication.exception.TokenFormatNotValidException;
import com.swifty.bank.server.core.domain.customer.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
@Component
public class JwtTokenUtil implements Serializable {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-expiration-millis}")
    private int accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration-millis}")
    private int refreshTokenExpiration;

    public UUID getUuidFromToken(String token) {
        if (token == null || token.isEmpty())
            throw new TokenFormatNotValidException("[ERROR] Token content cannot be empty");
        Claims claims = getClaimFromToken(token.split(" ")[1].trim());
        if (isTokenExpired(token))
            throw new TokenExpiredException("[ERROR] Token is expired, reissue it");
        if (!validateToken(token))
            throw new TokenContentNotValidException("[ERROR] Token content is not valid");
        return UUID.fromString(claims.get("id").toString());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Claims getClaimFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        if (expiration == null) {
            return false;
        }
        return expiration.before(new Date());
    }

    public TokenDto generateToken(Customer customer) {
        String accessToken = doGenerateToken(customer,
                "customer",
                new Date(new Date().getTime() + accessTokenExpiration * 1000)
        );
        String refreshToken = doGenerateToken(
                customer,
                "customer",
                new Date(new Date().getTime() + refreshTokenExpiration * 1000)
        );
        return new TokenDto(accessToken, refreshToken);
    }

    private String doGenerateToken(Customer customer, String subject, Date expir) {
        Claims claims = Jwts.claims();
        claims.put("id", customer.getId());
        claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("CUSTOMER")));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expir)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public Boolean validateToken(String token) {
        Claims claims = getClaimFromToken(token);
        return !isTokenExpired(token) && claims.get("id").toString().isEmpty();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaimFromToken(accessToken);

        if (claims.get("id").toString().isBlank() || claims.get("id").toString().isEmpty()) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("scopes").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        String subject = claims.getSubject();
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
