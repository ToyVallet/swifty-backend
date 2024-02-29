package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.service.CustomerApiService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
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
    public CustomerInfoResponse getCustomerInfo(UUID customerId) {


        CustomerInfoResponse customerInfoResponse = customerService.findCustomerInfoDtoByUuid(customerId)
                .orElseThrow(() -> new NoSuchElementException());


        return customerInfoResponse;
    }

    @Transactional
    @Override
    public void customerInfoUpdate(UUID customerId,
                                                CustomerInfoUpdateConditionRequest customerInfoUpdateCondition) {

        customerService.updateCustomerInfo(customerId, customerInfoUpdateCondition);

    }

    @Override
    public boolean confirmPassword(UUID customerId, String password) {
        Customer customer = customerService.findByUuid(customerId)
                .orElseThrow(() -> new NoSuchElementException());

        if (encoder.matches(password, customer.getPassword())) {
            return true;
        }

        return false;
    }

    @Transactional
    @Override
    public void resetPassword(UUID customerId, String newPassword) {
        customerService.updatePassword(customerId, newPassword);
    }

    @Transactional
    @Override
    public void customerWithdrawal(UUID customerId) {
        customerService.withdrawCustomer(customerId);
    }

}
