package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoDto;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerApiServiceImpl implements CustomerApiService {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public CustomerInfoResponse getCustomerInfo(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        CustomerInfoDto customerInfoDto = customerService.findCustomerInfoDtoByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException());

        return CustomerInfoResponse.builder()
                .name(customerInfoDto.getName())
                .phoneNumber(customerInfoDto.getPhoneNumber())
                .gender(customerInfoDto.getGender())
                .birthDate(customerInfoDto.getBirthDate())
                .nationality(customerInfoDto.getNationality())
                .customerStatus(customerInfoDto.getCustomerStatus())
                .build();
    }

    @Override
    public void customerInfoUpdate(String accessToken,
                                   CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        customerService.updateCustomerInfo(customerUuid, customerInfoUpdateCondition);

    }

    @Override
    public boolean confirmPassword(String accessToken, PasswordRequest passwordRequest) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        String password = passwordRequest.getPassword();

        Customer customer = customerService.findByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException());

        if (encoder.matches(password, customer.getPassword())) {
            return true;
        }

        return false;
    }

    @Override
    public void resetPassword(String accessToken, PasswordRequest passwordRequest) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        String newPassword = passwordRequest.getPassword();
        customerService.updatePassword(customerUuid, newPassword);
    }

    @Override
    public void customerWithdrawal(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        customerService.withdrawCustomer(customerUuid);
    }

}
