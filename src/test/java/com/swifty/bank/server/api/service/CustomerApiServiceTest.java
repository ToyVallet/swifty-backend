package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@SpringBootTest
@Testcontainers
class CustomerApiServiceTest {

    @Container
    private static MySQLContainer mysqlContainer  = new MySQLContainer("mysql:8.0.33")
            .withDatabaseName("bank_db")
            .withUsername("test")
            .withPassword("test");
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
    CustomerApiService customerApiService;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Test
    void getCustomerInfo() {
        //give
        Customer customer = saveCustomer("비밀번호");


        //when
        CustomerInfoResponse customerInfo = customerApiService.getCustomerInfo(customer.getId());

        //then
        Assertions.assertThat(customerInfo.getName()).isEqualTo(customerInfo.getName());
        Assertions.assertThat(customerInfo.getPhoneNumber()).isEqualTo(customerInfo.getPhoneNumber());
        Assertions.assertThat(customerInfo.getBirthDate()).isEqualTo(customerInfo.getBirthDate());
        Assertions.assertThat(customerInfo.getGender()).isEqualTo(customerInfo.getGender());
        Assertions.assertThat(customerInfo.getCustomerStatus()).isEqualTo(customerInfo.getCustomerStatus());
        Assertions.assertThat(customerInfo.getNationality()).isEqualTo(customerInfo.getNationality());
    }

    @Test
    void customerInfoUpdate() {
        //give
        Customer customer = saveCustomer("비밀번호");
        CustomerInfoUpdateConditionRequest conditionRequest = CustomerInfoUpdateConditionRequest.builder()
                .name("변경이름")
                .build();

        //when
        customerApiService.customerInfoUpdate(customer.getId(), conditionRequest);
        CustomerInfoResponse customerInfo = customerApiService.getCustomerInfo(customer.getId());


        //then
        Assertions.assertThat(customerInfo.getName()).isEqualTo(conditionRequest.getName());
    }

    @Test
    void confirmPassword() {
        //give
        String password = "비밀번호";
        Customer customer = saveCustomer(password);

        //when
        boolean isSamePassword = customerApiService.confirmPassword(customer.getId(),password);

        //then
        Assertions.assertThat(isSamePassword).isTrue();
    }

    @Test
    void resetPassword() {
        //give
        String password = "비밀번호";
        Customer customer = saveCustomer(password);
        String newPassword ="신규 비밀번호";

        //when
        customerApiService.resetPassword(customer.getId(),newPassword);
        boolean isSamePassword = customerApiService.confirmPassword(customer.getId(),newPassword);

        Assertions.assertThat(isSamePassword).isTrue();
    }

    @Test
    void customerWithdrawal() {
        //give
        String password = "비밀번호";
        Customer customer = saveCustomer(password);

        //when
        customerApiService.customerWithdrawal(customer.getId());

        Assertions.assertThatThrownBy(() -> customerApiService.getCustomerInfo(customer.getId()))
                .isInstanceOf(NoSuchElementException.class);


    }

    private Customer saveCustomer(String password) {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("테스터")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01077131548")
                .password(encoder.encode(password))
                .deviceId("디바이스아이디")
                .build();

        return customerRepository.save(customer);
    }
}