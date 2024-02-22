package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.GetVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.core.common.response.ResponseResult;

public interface PhoneAuthenticationService {
    ResponseResult<?> getVerificationCode(GetVerificationCodeRequest getVerificationCodeRequest);

    ResponseResult<?> sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest);

    ResponseResult<?> checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest);
}