package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request for verifying customer existence")
public class VerifyCustomerExistenceRequest {
    @NotNull
    @Size(min = 3, max = 14)
    @Schema(description = "국제번호 형식으로 입력해주세요",
            example = "+8201012345678",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;
}
