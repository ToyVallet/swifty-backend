package com.swifty.bank.server.api;

import com.redis.testcontainers.RedisContainer;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

@Transactional
@Testcontainers
@Disabled
@WebAppConfiguration
@ActiveProfiles("test")
public class ConfigureContainer {

    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.33")
            .withPassword("test")
            .withUsername("test")
            .withDatabaseName("test");

    @Container
    private static final RedisContainer redisContainer = new RedisContainer(RedisContainer.
            DEFAULT_IMAGE_NAME.withTag("6.2.6"))
            .withExposedPorts(6379);

    @BeforeAll
    public static void setupContainers( ) {
        mySQLContainer.start();
        redisContainer.start();

        System.setProperty("spring.data.redis.host", redisContainer.getHost());
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(6379).toString());
    }
}
