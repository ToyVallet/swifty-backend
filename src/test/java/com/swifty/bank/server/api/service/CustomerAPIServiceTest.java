package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@Slf4j
class CustomerAPIServiceTest {
    @Autowired
    private CustomerAPIService customerAPIService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private  BCryptPasswordEncoder encoder;


    @Test
    void getCustomerInfo() {
        //give
        Customer joinCustomer = join();

        //when
        ResponseResult<?> responseResult = customerAPIService.getCustomerInfo(joinCustomer.getId());
        CustomerInfoResponse customerInfoResponse = (CustomerInfoResponse) responseResult.getData();

        //then
        assertThat(responseResult.getResult()).isEqualTo(Result.SUCCESS);
        assertThat(customerInfoResponse.getName()).isEqualTo(joinCustomer.getName());
        assertThat(customerInfoResponse.getPhoneNumber()).isEqualTo(joinCustomer.getPhoneNumber());
        assertThat(customerInfoResponse.getGender()).isEqualTo(joinCustomer.getGender());
        assertThat(customerInfoResponse.getBirthDate()).isEqualTo(joinCustomer.getBirthDate());
        assertThat(customerInfoResponse.getNationality()).isEqualTo(joinCustomer.getNationality());
        assertThat(customerInfoResponse.getCustomerStatus()).isEqualTo(joinCustomer.getCustomerStatus());
    }

    @Test
    void customerInfoUpdate() {
        //give
        Customer joinCustomer = join();
        CustomerInfoUpdateConditionRequest conditionRequest = CustomerInfoUpdateConditionRequest.builder()
                .name("이름변경")
                .build();
        //when
        ResponseResult<?> responseResult = customerAPIService.customerInfoUpdate(joinCustomer.getId(), conditionRequest);

        //then
        assertThat(responseResult.getResult()).isEqualTo(Result.SUCCESS);
        assertThat(responseResult.getMessage()).isEqualTo("성공적으로 회원정보를 수정하였습니다.");
    }

    @Test
    void confirmPassword() {
        //give
        Customer joinCustomer = join();
        String inputPassword = "1234516";

        //when
        ResponseResult<?> responseResult = customerAPIService.confirmPassword(joinCustomer.getId(), inputPassword);

        //then
        assertThat(responseResult.getResult()).isEqualTo(Result.SUCCESS);
        assertThat(responseResult.getMessage()).isEqualTo("비밀번호가 일치합니다.");
    }

    @Test
    void resetPassword() {
        //give
        Customer joinCustomer = join();
        String inputNewPassword = "789456";

        //when
        ResponseResult<?> responseResult = customerAPIService.resetPassword(joinCustomer.getId(), inputNewPassword);

        //then
        assertThat(responseResult.getResult()).isEqualTo(Result.SUCCESS);
        assertThat(responseResult.getMessage()).isEqualTo("성공적으로 비밀번호를 변경하였습니다.");
    }

    @Test
    void customerWithdrawal() {
        //give
        Customer joinCustomer = join();
        //when
        ResponseResult<?> responseResult = customerAPIService.customerWithdrawal(joinCustomer.getId());
        //then
        assertThat(responseResult.getResult()).isEqualTo(Result.SUCCESS);
        assertThat(responseResult.getMessage()).isEqualTo("회원탈퇴를 성공적으로 완료하였습니다.");
    }

    private Customer join (){
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("테스트")
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(Nationality.KOREA)
                .gender(Gender.MALE)
                .birthDate("19950617")
                .phoneNumber("01055551111")
                .password(encoder.encode("123456"))
                .deviceId("galaxy")
                .build();

        return customerRepository.save(customer);
    }
}