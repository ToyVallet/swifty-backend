package com.swifty.bank.server.core.domain.sms.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendVerificationCodeRequest {
    @NotNull
    @NotBlank
    private String deviceId;
    @NotNull
    @NotBlank
    private String phoneNumber;
}
