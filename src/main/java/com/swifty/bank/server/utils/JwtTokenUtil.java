package com.swifty.bank.server.utils;

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
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
@Component
public class JwtTokenUtil implements Serializable {
    @Value("${jwt.secret}")
    private String secretKey;
    private int expiration = 15 * 60;

    public String getUuidFromToken(String token) {
        Claims claims = getClaimFromToken(token);
        return claims.get("id").toString();
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
        return expiration.before(new Date( ));
    }

    public String generateToken(Customer customer) {
        return doGenerateToken(customer, "customer");
    }

    private String doGenerateToken(Customer customer, String subject) {
        Claims claims = Jwts.claims( );
        claims.put("id", customer.getId());
        claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("CUSTOMER")));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public Boolean validateToken(String token) {
        final String uuid = getUuidFromToken(token);
        return !uuid.isEmpty();
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
        String subject = claims.getSubject( );
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
