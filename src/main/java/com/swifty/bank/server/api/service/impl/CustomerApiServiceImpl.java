package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerApiServiceImpl implements CustomerApiService {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public CustomerInfoResponse getCustomerInfo(String  accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        CustomerInfoResponse customerInfoResponse = customerService.findCustomerInfoDtoByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException());


        return customerInfoResponse;
    }

    @Transactional
    @Override
    public void customerInfoUpdate(String  accessToken,
                                                CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        customerService.updateCustomerInfo(customerUuid, customerInfoUpdateCondition);

    }

    @Override
    public boolean confirmPassword(String  accessToken, PasswordRequest passwordRequest) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        String password = passwordRequest.getPassword();

        Customer customer = customerService.findByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException());

        if (encoder.matches(password, customer.getPassword())) {
            return true;
        }

        return false;
    }

    @Transactional
    @Override
    public void resetPassword(String  accessToken, PasswordRequest passwordRequest) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        String newPassword = passwordRequest.getPassword();
        customerService.updatePassword(customerUuid, newPassword);
    }

    @Transactional
    @Override
    public void customerWithdrawal(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);
        customerService.withdrawCustomer(customerUuid);
    }

}
