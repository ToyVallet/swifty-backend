package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface CustomerApiService {
    ResponseResult<?> getCustomerInfo(String customerUuid);

    ResponseResult<?> customerInfoUpdate(String customerUuid,
                                         CustomerInfoUpdateConditionRequest customerInfoUpdateCondition);

    ResponseResult<?> confirmPassword(String customerUuid, String password);

    ResponseResult<?> resetPassword(String customerUuid, String newPassword);

    ResponseResult<?> customerWithdrawal(String customerUuid);
}
