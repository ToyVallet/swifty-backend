package com.swifty.bank.server.core.domain.sms.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Form to get verification code")
public class CheckVerificationCodeRequest {
    @NotNull
    @NotBlank
    @Schema(description = "phone's device id to distinguish users")
    private String deviceId;

    @Schema(description = "phone's number which be sent with verification code")
    private String phoneNumber;
    @Schema(description = "verification which phone's owner got from server")
    private String verificationCode;
}