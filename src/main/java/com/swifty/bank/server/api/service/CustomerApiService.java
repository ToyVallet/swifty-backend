package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.UUID;

public interface CustomerApiService {
    CustomerInfoResponse getCustomerInfo(UUID customerId);

    void customerInfoUpdate(UUID customerId,
                                CustomerInfoUpdateConditionRequest customerInfoUpdateCondition);

    boolean confirmPassword(UUID customerId, String password);

    void resetPassword(UUID customerId, String newPassword);

    void customerWithdrawal(UUID customerId);
}
