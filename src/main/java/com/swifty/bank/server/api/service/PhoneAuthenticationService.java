package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.GetVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface PhoneAuthenticationService {
    ResponseResult<?> stealVerificationCode(GetVerificationCodeRequest getVerificationCodeRequest);

    ResponseResult<?> sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest);

    ResponseResult<?> checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest);
}