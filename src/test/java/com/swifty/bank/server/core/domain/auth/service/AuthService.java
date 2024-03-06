package com.swifty.bank.server.core.domain.auth.service;

import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.swifty.bank.server.core.domain.ConfigureContainer;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
public class AuthService extends ConfigureContainer {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Test
    public void joinTest( ) {
        JoinDto joinDto = new JoinDto(null,"asdasd", Nationality.KOREA,"01000001111","23213","sadasd", Gender.MALE,"19950601", UserRole.CUSTOMER);
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(joinDto.getName())
                .gender(joinDto.getGender())
                .birthDate(joinDto.getBirthDate())
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(joinDto.getNationality())
                .phoneNumber(joinDto.getPhoneNumber())
                .password(encoder.encode(joinDto.getPassword()))
                .deviceId(joinDto.getDeviceId())
                .roles(joinDto.getRoles())
                .build();

        Customer joinCustomer = customerService.join(joinDto);

        assertThat(joinCustomer.getId()).isNotNull();
        assertThat(joinCustomer.getName()).isEqualTo(joinDto.getName());
        assertThat(joinCustomer.getDeviceId()).isEqualTo(joinDto.getDeviceId());
        assertThat(joinCustomer.getPhoneNumber()).isEqualTo(joinDto.getPhoneNumber());
        assertThat(joinCustomer.getGender()).isEqualTo(joinDto.getGender());
        assertThat(joinCustomer.getBirthDate()).isEqualTo(joinDto.getBirthDate());
        assertThat(joinCustomer.getNationality()).isEqualTo(joinDto.getNationality());
        assertThat(joinCustomer.getCustomerStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(encoder.matches(joinDto.getPassword(),joinCustomer.getPassword())).isTrue();
        assertThat(joinCustomer.getRoles()).isEqualTo(joinDto.getRoles());
    }
}
