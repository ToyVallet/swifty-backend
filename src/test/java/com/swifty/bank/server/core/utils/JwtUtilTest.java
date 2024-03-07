package com.swifty.bank.server.core.utils;

import com.swifty.bank.server.core.domain.customer.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.jsonwebtoken.Jwts.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JwtUtilTest {
    private Claims claims;

    public JwtUtilTest(JwtUtil jwtUtil) {
        this.claims = claims();
    }

    @Test
    void generateTokenTest( ) {
        String generatedToken = JwtUtil.generateToken(claims, new Date());

        assertThat(generatedToken.startsWith("ey"));
    }

    @Test
    void removePrefixTest( ) {
        String rawJwt = "Bearer " + JwtUtil.generateToken(claims, new Date());

        assertThat(JwtUtil.removePrefix(rawJwt).startsWith("ey"));
    }

    @Test
    void extractJwtFromCurrentRequestHeader( ) {
        assertThat(true);
        // 어떻게 처리할지 논의 필요
    }

    @Test
    void getSubjectTest( ) {
        claims.setSubject("Subject");
        String jwt = JwtUtil.generateToken(claims, new Date());
        assertThat(JwtUtil.getSubject(jwt)).isEqualTo("Subject");
    }

    @Test
    void validateTokenTest( ) {
        String jwt = JwtUtil.generateToken(claims, new Date());

        assertThatThrownBy(() -> JwtUtil.validateToken(jwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isExpiredTrue( ) {
        String expiredJwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))
        ); // 하루 전 Jwt 토큰
        assertThat(JwtUtil.isExpiredToken(expiredJwt));
    }

    @Test
    void isExpiredFalse( ) {
        String notExpiredJwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        ); // 하루 전 Jwt 토큰

        assertThat(!JwtUtil.isExpiredToken(notExpiredJwt));
    }

    @Test
    void getValueByKeyWithObjectPerformWell( ) {
        UUID customerUuid = UUID.randomUUID();
        final String key = "customerUuid";
        claims.put(key, customerUuid);
        String jwt = JwtUtil.generateToken(claims, new Date());

        assertThat(JwtUtil.getValueByKeyWithObject(jwt, key, UUID.class))
                .isEqualTo(customerUuid);
    }

    @Test
    void invalidObjectInGetValueByKeyWithObject( ) {
        assertThatThrownBy(() -> JwtUtil.getValueByKeyWithObject("", "", Customer.class))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

