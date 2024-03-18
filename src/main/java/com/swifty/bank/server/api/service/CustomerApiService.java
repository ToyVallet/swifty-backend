package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.request.PasswordRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;

public interface CustomerApiService {
    CustomerInfoResponse getCustomerInfo(String accessToken);

    void customerInfoUpdate(String accessToken, CustomerInfoUpdateConditionRequest customerInfoUpdateCondition);

    boolean confirmPassword(String accessToken, String keypadToken, PasswordRequest passwordRequest);

    void resetPassword(String accessToken, PasswordRequest passwordRequest);

    void customerWithdrawal(String accessToken);

    CreateSecureKeypadResponse createSecureKeypad();
}