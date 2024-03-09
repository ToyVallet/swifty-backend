package com.swifty.bank.server.api.controller.dto.sms.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckVerificationCodeRequest {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 18)
    @Pattern(regexp = "^\\+[0-9]{1,17}")
    @Schema(description = "국제번호 형식으로 입력해주세요.",
            example = "+821012345678",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;

    @NotNull
    @Schema(example = "123456",
            requiredMode = RequiredMode.REQUIRED)
    private String verificationCode;
}