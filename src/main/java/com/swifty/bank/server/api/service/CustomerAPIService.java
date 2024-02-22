package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.common.response.ResponseResult;
import java.util.UUID;

public interface CustomerAPIService {
    ResponseResult<?> getCustomerInfo(UUID customerUuid);

    ResponseResult<?> customerInfoUpdate(UUID customerUuid,
                                         CustomerInfoUpdateConditionRequest customerInfoUpdateCondition);

    ResponseResult<?> confirmPassword(UUID customerUuid, String password);

    ResponseResult<?> resetPassword(UUID customerUuid, String newPassword);

    ResponseResult<?> customerWithdrawal(UUID customerUuid);
}
