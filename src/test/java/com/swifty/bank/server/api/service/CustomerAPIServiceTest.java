package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.utils.RedisUtil;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;


@SpringBootTest
@Testcontainers
@Slf4j
class CustomerAPIServiceTest {

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

    @Autowired
    RedisUtil redisUtil;

    @Test
    void getCustomerInfo() {
        join();

        redisUtil.setRedisStringValue("test","test");

        String getDataFromRedis = redisUtil.getRedisStringValue("test");


        log.info("redis={}",getDataFromRedis);

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

    private Customer join() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("테스터")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01045457788")
                .password("123456")
                .deviceId("iphone")
                .build();

        return customerRepository.save(customer);
    }
}