package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.service.dto.CheckVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendMessageRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendVerificationCodeRequest;

public interface PhoneAuthenticationService {
    ResponseResult<?> sendMessage(SendMessageRequest sendMessageRequest);

    ResponseResult<?> sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest);

    ResponseResult<?> checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest);
}
