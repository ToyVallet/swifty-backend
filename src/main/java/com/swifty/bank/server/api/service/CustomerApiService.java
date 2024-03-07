package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.UUID;

public interface CustomerApiService {
    CustomerInfoResponse getCustomerInfo(UUID customerUuid);

    void customerInfoUpdate(UUID customerUuid,
                                CustomerInfoUpdateConditionRequest customerInfoUpdateCondition);

    boolean confirmPassword(UUID customerUuid, String password);

    void resetPassword(UUID customerUuid, String newPassword);

    void customerWithdrawal(UUID customerUuid);
}
