package com.swifty.bank;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class CustomerApiServiceTest {
    @Container
    private static MySQLContainer mysqlContainer = new MySQLContainer("mysql:8.0.33")
            .withDatabaseName("bank_db")
            .withUsername("test")
            .withPassword("test");
    @Container
    private static GenericContainer redisContainer = new GenericContainer("redis")
            .withExposedPorts(6379);


    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getExposedPorts);
    }

    @Test
    public void testEquals() {
        int a = 1;
        Assertions.assertThat(1).isEqualTo(a);
    }
}