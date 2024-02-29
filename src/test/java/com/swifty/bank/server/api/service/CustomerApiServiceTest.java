package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CustomerApiServiceTest {
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

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerApiService customerApiService;

    @Autowired
    BCryptPasswordEncoder encoder;

//    @Test
//    public void saveCustomer() {
//        Customer customer = Customer.builder()
//                .id(UUID.randomUUID())
//                .name("테스터")
//                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
//                .nationality(Nationality.KOREA)
//                .phoneNumber("01077131548")
//                .password(encoder.encode("1234"))
//                .deviceId("디바이스아이디")
//                .build();
//        customerRepository.save(customer);
//    }

    @Test
    public void testEquals() {
        int a = 1;
        Assertions.assertThat(1).isEqualTo(a);
    }

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