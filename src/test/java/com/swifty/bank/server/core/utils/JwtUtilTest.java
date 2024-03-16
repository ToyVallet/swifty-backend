package com.swifty.bank.server.core.utils;

import com.swifty.bank.server.core.domain.customer.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@ContextConfiguration(locations = {"classpath:config/application.yaml"})
@WebAppConfiguration
public class JwtUtilTest {
    private static Claims claims;

    @BeforeAll
    public static void JwtUtilInit() {
        claims = Jwts.claims();
    }

    @Test
    void generateTokenTest( ) {
        String generatedToken = JwtUtil.generateToken(claims, new Date());

        assertThat(generatedToken.startsWith("ey"));
    }

    @Test
    void removePrefixTest( ) {
        String rawJwt = "Bearer " + JwtUtil.generateToken(claims, new Date());

        assertThat(JwtUtil.removeType(rawJwt).startsWith("ey"));
    }

    @Test
    void getSubjectTest( ) {
        claims.setSubject("Subject");
        String jwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        ); // 하루 후 Jwt 토큰
        assertThat(JwtUtil.getSubject(jwt)).isEqualTo("Subject");
    }

    @Test
    void validateTokenTest( ) {
        String jwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        ); // 하루 후 Jwt 토큰

        JwtUtil.validateToken(jwt);
    }

    @Test
    void isExpiredToken( ) {
        String expiredJwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))
        ); // 하루 후 Jwt 토큰

        assertThatThrownBy(() -> JwtUtil.validateToken(expiredJwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isExpiredTrue( ) {
        String expiredJwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))
        ); // 하루 전 Jwt 토큰
        assertThatThrownBy(() -> JwtUtil.isExpiredToken(expiredJwt))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void isExpiredFalse( ) {
        String notExpiredJwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        ); // 하루 후 Jwt 토큰

        assertThat(!JwtUtil.isExpiredToken(notExpiredJwt));
    }

    @Test
    void getValueByKeyWithObjectPerformWell( ) {
        UUID customerUuid = UUID.randomUUID();
        final String key = "customerUuid";
        claims.put(key, customerUuid);
        String jwt = JwtUtil.generateToken(claims,
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        ); // 하루 후 Jwt 토큰

        assertThat(JwtUtil.getValueByKeyWithObject(jwt, key, UUID.class))
                .isEqualTo(customerUuid);
    }

    @Test
    void invalidObjectInGetValueByKeyWithObject( ) {
        assertThatThrownBy(() -> JwtUtil.getValueByKeyWithObject("", "", Customer.class))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

