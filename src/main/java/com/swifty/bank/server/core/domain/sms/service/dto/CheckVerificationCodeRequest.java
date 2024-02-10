package com.swifty.bank.server.core.domain.sms.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckVerificationCodeRequest {
    @NotNull
    @NotBlank
    private String deviceId;
    @NotNull
    @NotBlank
    private String phoneNumber;
    @NotNull
    @NotBlank
    private String verificationCode;
}
