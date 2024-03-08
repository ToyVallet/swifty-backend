package com.swifty.bank.server.api.controller.dto.sms.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StealVerificationCodeRequest {
    @NotNull
    @Size(min = 3, max = 14)
    @Schema(example = "+12051234567",
            requiredMode = RequiredMode.REQUIRED)
    @Pattern(regexp = "^\\+82\\d+$\n")  // start with +82 and only digits
    private String phoneNumber;
}
