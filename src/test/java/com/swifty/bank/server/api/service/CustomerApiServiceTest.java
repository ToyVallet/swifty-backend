package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

class CustomerApiServiceTest {


    @Container
    private static MySQLContainer mysqlContainer  = new MySQLContainer("mysql:8.0.33")
            .withDatabaseName("bank_db")
            .withUsername("root")
            .withPassword("swifty,Bank,DB2024");
    @Container
    private static GenericContainer redisContainer = new GenericContainer("redis")
            .withExposedPorts(6379);


    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getExposedPorts);
    }

    @Autowired
    CustomerRepository customerRepository;



    @Test
    void getCustomerInfo() {
    }

    @Test
    void customerInfoUpdate() {
    }

    @Test
    void confirmPassword() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void customerWithdrawal() {
    }
}