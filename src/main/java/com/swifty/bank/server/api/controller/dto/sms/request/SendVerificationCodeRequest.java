package com.swifty.bank.server.api.controller.dto.sms.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request for to send verification code")
public class SendVerificationCodeRequest {
    @NotNull
    @Size(max = 14, min = 3)
    @Schema(description = "start with +1 and only digits 0-9 without dash", example = "+12051234567",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;
}