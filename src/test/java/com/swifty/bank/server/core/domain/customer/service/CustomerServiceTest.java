package com.swifty.bank.server.core.domain.customer.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Spy
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private CustomerServiceImpl customerService;


    @Test
    @DisplayName("회원가입")
    void join() {
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
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

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

    @Test
    @DisplayName("회원UUID로 회원조회 ")
    void findByUuid() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("디바이스아이디")
                .roles(UserRole.CUSTOMER)
                .build();
        when(customerRepository.findOneByUUID(any(UUID.class)))
                .thenReturn(Optional.ofNullable(customer));

        Customer findCustomer = customerService.findByUuid(customer.getId()).get();

        assertThat(findCustomer.getId()).isEqualTo(customer.getId());

    }

    @Test
    @DisplayName("회원연락처로 회원조회 ")
    void findByPhoneNumber() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("디바이스아이디")
                .roles(UserRole.CUSTOMER)
                .build();
        when(customerRepository.findOneByPhoneNumber(anyString()))
                .thenReturn(Optional.ofNullable(customer));

        Customer findCustomer = customerService.findByPhoneNumber(customer.getPhoneNumber()).get();

        assertThat(findCustomer.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
    }

    @Test
    @DisplayName("회원기기정보로 회원조회 ")
    void findByDeviceId() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("디바이스아이디")
                .roles(UserRole.CUSTOMER)
                .build();
        when(customerRepository.findOneByDeviceId(anyString()))
                .thenReturn(Optional.ofNullable(customer));

        Customer findCustomer = customerService.findByDeviceId(customer.getDeviceId()).get();

        assertThat(findCustomer.getDeviceId()).isEqualTo(customer.getDeviceId());
    }

    @Test
    @DisplayName("회원 연락처 변경")
    void updatePhoneNumber() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("디바이스아이디")
                .roles(UserRole.CUSTOMER)
                .build();
        String updatePhoneNumber = "01011112222";

        when(customerRepository.findOneByUUID(any(UUID.class)))
                .thenReturn(Optional.ofNullable(customer));


        Customer updateCustomer = customerService.updatePhoneNumber(customer.getId(),updatePhoneNumber);

        assertThat(updateCustomer.getPhoneNumber()).isEqualTo(updatePhoneNumber);
    }

    @Test
    @DisplayName("회원 기기정보 변경")
    void updateDeviceId() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("갤럭시")
                .roles(UserRole.CUSTOMER)
                .build();
        String updateDeviceId = "아이폰";

        when(customerRepository.findOneByUUID(any(UUID.class)))
                .thenReturn(Optional.ofNullable(customer));


        Customer updateCustomer = customerService.updateDeviceId(customer.getId(),updateDeviceId);

        assertThat(updateCustomer.getDeviceId()).isEqualTo(updateDeviceId);
    }

    @Test
    @DisplayName("회원정보 변경")
    void updateCustomerInfo() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("갤럭시")
                .roles(UserRole.CUSTOMER)
                .build();

        CustomerInfoUpdateConditionRequest updateConditionRequest = CustomerInfoUpdateConditionRequest.builder()
                .name("이름변경")
                .build();

        when(customerRepository.findOneByUUID(any(UUID.class)))
                .thenReturn(Optional.ofNullable(customer));


        Customer updateCustomerInfo = customerService.updateCustomerInfo(customer.getId(),updateConditionRequest);

        assertThat(updateCustomerInfo.getName()).isEqualTo(updateConditionRequest.getName());
    }

    @Test
    @DisplayName("회원UUID로 회원정보 조회")
    void findCustomerInfoDtoByUuid() {
        CustomerInfoResponse customerInfoResponse = new CustomerInfoResponse("이름","01000001111",Gender.MALE,"19990909",Nationality.KOREA,CustomerStatus.ACTIVE);

        when(customerRepository.findCustomerInfoResponseByUUID(any(UUID.class)))
                .thenReturn(Optional.ofNullable(customerInfoResponse));

        CustomerInfoResponse findCustomerInfoResponse = customerService.findCustomerInfoDtoByUuid(UUID.randomUUID()).get();

        assertThat(findCustomerInfoResponse.getName()).isEqualTo(customerInfoResponse.getName());
    }

    @Test
    @DisplayName("회원 비밀번호 변경")
    void updatePassword() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("갤럭시")
                .roles(UserRole.CUSTOMER)
                .build();

        String updatePassword = "비밀번호 변경";

        when(customerRepository.findOneByUUID(any(UUID.class)))
                .thenReturn(Optional.ofNullable(customer));

        assertDoesNotThrow(() ->  customerService.updatePassword(customer.getId(),updatePassword));
    }

    @Test
    @DisplayName("회원 탈퇴")
    void withdrawCustomer() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("이름")
                .gender(Gender.MALE)
                .birthDate("19991212")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .phoneNumber("01000001111")
                .password(encoder.encode("비밀번호"))
                .deviceId("갤럭시")
                .roles(UserRole.CUSTOMER)
                .build();

        when(customerRepository.findOneByUUID(any(UUID.class)))
                .thenReturn(Optional.ofNullable(customer));

        assertDoesNotThrow(() ->  customerService.withdrawCustomer(customer.getId()));
    }
}