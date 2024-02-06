package com.swifty.bank.server.core.domain.sms.service.dto;

import lombok.Data;

@Data
public class CheckVerificationCodeRequest {
    private String phoneNumber;
    private String verificationCode;
}
