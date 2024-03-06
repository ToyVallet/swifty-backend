package com.swifty.bank.server.core.domain;

import com.redis.testcontainers.RedisContainer;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
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
    }
}
