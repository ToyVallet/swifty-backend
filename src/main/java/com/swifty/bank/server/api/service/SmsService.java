package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.StealVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.response.CheckVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.sms.response.SendVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.sms.response.StealVerificationCodeResponse;

public interface SmsService {
    StealVerificationCodeResponse stealVerificationCode(StealVerificationCodeRequest stealVerificationCodeRequest);

    SendVerificationCodeResponse sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest);

    CheckVerificationCodeResponse checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest);
}