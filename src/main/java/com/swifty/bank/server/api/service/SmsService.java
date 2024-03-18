package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.StealVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SendVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.StealVerificationCodeResponse;

public interface SmsService {
    StealVerificationCodeResponse stealVerificationCode(StealVerificationCodeRequest stealVerificationCodeRequest);

    SendVerificationCodeResponse sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest);

    CheckVerificationCodeResponse checkVerificationCode(CheckVerificationCodeRequest checkVerificationCodeRequest);
}