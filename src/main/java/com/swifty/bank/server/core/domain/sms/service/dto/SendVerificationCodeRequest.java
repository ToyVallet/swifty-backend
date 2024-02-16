package com.swifty.bank.server.core.domain.sms.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request for to send verification code")
public class SendVerificationCodeRequest {
    @NotNull
    @NotBlank
    @Schema(description = "device Id from replied device")
    private String deviceId;
    @NotNull
    @NotBlank
    @Schema(description = "where server send a sms")
    private String phoneNumber;
}