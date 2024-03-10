package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.UUID;

public interface CustomerApiService {
    CustomerInfoResponse getCustomerInfo(String accessToken);

    void customerInfoUpdate(String accessToken, CustomerInfoUpdateConditionRequest customerInfoUpdateCondition);

    boolean confirmPassword(String accessToken, PasswordRequest passwordRequest);

    void resetPassword(String accessToken, PasswordRequest passwordRequest);

    void customerWithdrawal(String accessToken);
}
