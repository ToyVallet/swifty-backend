package com.swifty.bank.server.core.domain.sms.service.dto;

import lombok.Data;

@Data
public class SendVerificationCodeRequest {
    private String phoneNumber;
}
