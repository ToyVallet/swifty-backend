package com.swifty.bank.server.core.domain.sms.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Form to get verification code")
public class CheckVerificationCodeRequest {
    @NotNull
    @NotBlank
    @Schema(description = "phone's device id to distinguish users")
    private String deviceId;

    @NotNull
    @Size(max = 14, min = 3)
    @Pattern(regexp = "^\\d+$\n")
    @Schema(description = "start with +82 and only digits 0-9 without dash", example = "+8201012345678",
            required = true)
    private String phoneNumber;
    @Schema(description = "verification which phone's owner got from server")
    private String verificationCode;
}