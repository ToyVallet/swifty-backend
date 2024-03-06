package com.swifty.bank.server.core.domain;

import com.redis.testcontainers.RedisContainer;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

@Transactional
@Testcontainers
@Disabled
@ContextConfiguration(initializers = ConfigureContainer.IntegrationTestInitializer.class)
@ActiveProfiles("test")
public class ConfigureContainer {


    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.33")
            .withPassword("test")
            .withUsername("test")
            .withDatabaseName("bank_db");

    @Container
    private static final RedisContainer redisContainer = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("6.2.6"));


    @BeforeAll
    public static void setupContainers( ) {
        mySQLContainer.start();
        redisContainer.start();
    }

    static class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            Map<String, String> properties = new HashMap<>();

            setDatabaseProperties(properties);
            setRedisProperties(properties);
            setJwtProperties(properties);
            setTwilio(properties);

            TestPropertyValues.of(properties).applyTo(applicationContext);
        }
        private void setDatabaseProperties(Map<String, String> properties) {
            properties.put("spring.datasource.url", mySQLContainer.getJdbcUrl());
            properties.put("spring.datasource.username", mySQLContainer.getUsername());
            properties.put("spring.datasource.password", mySQLContainer.getPassword());
        }
        private void setRedisProperties(Map<String, String> properties) {
            properties.put("spring.redis.host", redisContainer.getHost());
            properties.put("spring.redis.port", redisContainer.getFirstMappedPort().toString());
        }
        private void setJwtProperties(Map<String, String> properties) {
            properties.put("jwt.secret", JwtConstants.jwtToken);
            properties.put("jwt.temporary-token-expiration-seconds", JwtConstants.temporaryTokenExpirationSecond);
            properties.put("jwt.access-token-expiration-seconds", JwtConstants.accessTokenExpirationSecond);
            properties.put("jwt.refresh-token-expiration-seconds", JwtConstants.refreshTokenExpirationSecond);
            properties.put("jwt.redis.temporary-token-minutes", JwtConstants.temporaryTokenMinutes);
            properties.put("jwt.redis.otp-timeout-minutes", JwtConstants.otpTimeoutMinutes);
        }

        private void setTwilio(Map<String, String> properties) {
            properties.put("TWILIO_ACCOUNT_SID", TwilioConstants.accountSid);
            properties.put("TWILIO_AUTH_TOKEN", TwilioConstants.authToken);
            properties.put("TWILIO_OUTGOING_SMS_NUMBER", TwilioConstants.smsNumber);
            properties.put("TWILIO_VERIFY_SID", TwilioConstants.verifySid);
        }
    }

    @Getter
    private static class JwtConstants {
        @Value("${jwt.secret}")
        private static String jwtToken;
        @Value("${jwt.temporary-token-expiration-seconds}")
        private static String temporaryTokenExpirationSecond;
        @Value("${jwt.access-token-expiration-seconds}")
        private static String accessTokenExpirationSecond;
        @Value("${jwt.refresh-token-expiration-seconds}")
        private static String refreshTokenExpirationSecond;
        @Value("${jwt.redis.temporary-token-minutes}")
        private static String temporaryTokenMinutes;
        @Value("${jwt.otp-timeout-minutes}")
        private static String otpTimeoutMinutes;
    }

    @Getter
    private static class TwilioConstants {
        @Value("TWILIO_ACCOUNT_SID")
        private static String accountSid;
        @Value("TWILIO_AUTH_TOKEN")
        private static String authToken;
        @Value("TWILIO_OUTGOING_SMS_NUMBER")
        private static String smsNumber;
        @Value("TWILIO_VERIFY_SID")
        private static String verifySid;
    }
}
