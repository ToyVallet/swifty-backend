package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.ConfigureContainer;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.core.common.authentication.constant.UserRole;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class AuthenticationApiServiceTest extends ConfigureContainer {
    @Autowired
    private AuthenticationApiService authenticationApiService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private static CustomerService customerService;

    @BeforeAll
    public static void initCustomer( ) {
        JoinDto joinDto = new JoinDto(null, "Taylor Swift", Nationality.KOREA, "+821011111111",
                "000000", "iPhone", Gender.FEMALE,
                "990101", UserRole.CUSTOMER);

        customerService.join(joinDto);
    }

    @Test
    public void checkLoginAvailabilityWithNonExistCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("John Doe")
                .phoneNumber("+821012345678")
                .mobileCarrier("KT")
                .residentRegistrationNumber("0101014")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        assertThat(res.getIsAvailable());
        assertThat(!res.getTemporaryToken().isEmpty());
    }

    @Test
    public void checkLoginAvailabilityWithExistValidCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821011111111")
                .mobileCarrier("KT")
                .residentRegistrationNumber("9901012")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        assertThat(res.getIsAvailable());
        assertThat(!res.getTemporaryToken().isEmpty());
    }

    @Test
    public void checkLoginAvailabilityWithExistNotValidCustomer( ) {
        CheckLoginAvailabilityRequest req = CheckLoginAvailabilityRequest.builder()
                .name("Taylor Swift")
                .phoneNumber("+821011111111")
                .mobileCarrier("KT")
                .residentRegistrationNumber("9901011")
                .build();

        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(req);

        assertThat(res.getIsAvailable());
        assertThat(!res.getTemporaryToken().isEmpty());
    }
}
