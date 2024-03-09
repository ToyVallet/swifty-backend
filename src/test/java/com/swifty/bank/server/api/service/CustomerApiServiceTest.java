package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@SpringBootTest
class CustomerApiServiceTest {

    @Autowired
    private CustomerApiService customerApiService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthenticationService authenticationService;

    private Validator validator;
    private final String name = "테스트";
    private final Gender gender = Gender.MALE;
    private final String birthDate = "19950505";
    private final Nationality nationality = Nationality.KOREA;
    private final String phoneNumber = "01011115555";
    private final String password = "123456";
    private final String deviceId = "갤럭시";
    private final UserRole userRole = UserRole.CUSTOMER;
    private String accessToken;

    @BeforeEach
    private void join(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        JoinDto joinDto = JoinDto.builder()
                .name(name)
                .gender(gender)
                .birthDate(birthDate)
                .nationality(nationality)
                .phoneNumber(phoneNumber)
                .password(password)
                .deviceId(deviceId)
                .roles(userRole)
                .build();

        Customer join = customerService.join(joinDto);
        accessToken = authenticationService.createAccessToken(join.getId());
    }

    @Test
    @DisplayName("회원정보 조회 - 성공케이스")
    void getCustomerInfo() {

        CustomerInfoResponse customerInfo = customerApiService.getCustomerInfo(accessToken);

        assertThat(customerInfo.getName()).isEqualTo(name);
        assertThat(customerInfo.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(customerInfo.getBirthDate()).isEqualTo(birthDate);
        assertThat(customerInfo.getGender()).isEqualTo(gender);
        assertThat(customerInfo.getNationality()).isEqualTo(nationality);
        assertThat(customerInfo.getCustomerStatus()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("회원정보 업데이트 - 성공케이스")
    void customerInfoUpdate() {
        CustomerInfoUpdateConditionRequest updateConditionRequest = CustomerInfoUpdateConditionRequest.builder()
                .phoneNumber("01055554444")
                .build();


        assertDoesNotThrow(() -> customerApiService.customerInfoUpdate(accessToken,updateConditionRequest));
    }


    @Test
    @DisplayName("비밀번호 일치여부 - 성공케이스")
    void confirmPassword() {
        PasswordRequest passwordRequest = new PasswordRequest(password);

        Set<ConstraintViolation<PasswordRequest>> violations  = validator.validate(passwordRequest);
        boolean  isMatchPassword= customerApiService.confirmPassword(accessToken, passwordRequest);

        assertThat(violations.size()).isEqualTo(0);
        assertThat(isMatchPassword).isTrue();
    }

    @Test
    @DisplayName("비밀번호 일치여부 - 실패케이스 - 비밀번호는 6자리")
    void confirmPassword_6자리_실패() {
        PasswordRequest passwordRequest = new PasswordRequest(password+1);

        Set<ConstraintViolation<PasswordRequest>> violations  = validator.validate(passwordRequest);
        boolean  isMatchPassword= customerApiService.confirmPassword(accessToken, passwordRequest);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(isMatchPassword).isFalse();
    }

    @Test
    @DisplayName("비밀번호 일치여부 - 실패케이스 - 비밀번호는 6자리")
    void confirmPassword_불일치() {
        PasswordRequest passwordRequest = new PasswordRequest("123123");

        Set<ConstraintViolation<PasswordRequest>> violations  = validator.validate(passwordRequest);
        boolean  isMatchPassword= customerApiService.confirmPassword(accessToken, passwordRequest);

        assertThat(violations.size()).isEqualTo(0);
        assertThat(isMatchPassword).isFalse();
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공케이스")
    void resetPassword() {

        PasswordRequest newPasswordRequest = new PasswordRequest("789456");
        Set<ConstraintViolation<PasswordRequest>> violations  = validator.validate(newPasswordRequest);

        assertThat(violations.size()).isEqualTo(0);
        assertDoesNotThrow(() -> customerApiService.resetPassword(accessToken,newPasswordRequest));
    }

    @Test
    @DisplayName("비밀번호는 6자리로 변경가능 - 실패케이스")
    void resetPassword_6자리() {

        PasswordRequest newPasswordRequest = new PasswordRequest("789456");
        Set<ConstraintViolation<PasswordRequest>> violations  = validator.validate(newPasswordRequest);

        assertThat(violations.size()).isEqualTo(0);
        assertDoesNotThrow(() -> customerApiService.resetPassword(accessToken,newPasswordRequest));
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패케이스")
    void resetPassword_실패() {
        PasswordRequest newPasswordRequest = new PasswordRequest("789412312356");

        Set<ConstraintViolation<PasswordRequest>> violations  = validator.validate(newPasswordRequest);

        assertThat(violations.size()).isEqualTo(1);
        assertDoesNotThrow(() -> customerApiService.resetPassword(accessToken,newPasswordRequest));
    }

    @Test
    @DisplayName("회원탈퇴 - 성공케이스")
    void customerWithdrawal() {
        assertDoesNotThrow(() ->  customerApiService.customerWithdrawal(accessToken));
    }
}