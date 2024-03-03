package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request for verifying customer existence")
public class VerifyCustomerExistenceRequest {
    @NotNull
    @NotBlank
    @Schema(description = "고객 이름",
            example = "김성명",
            requiredMode = RequiredMode.REQUIRED)
    private String name;
    @NotNull
    @NotBlank
    @Schema(description = "고객 주민등록번호 앞 7자리",
            example = "0012024",
            requiredMode = RequiredMode.REQUIRED)
    private String residentRegistrationNumber;
    @NotNull
    @NotBlank
    @Schema(description = "통신사",
            example = "KT",
            requiredMode = RequiredMode.REQUIRED)
    private String MobileCarrier;

    @NotNull
    @Size(min = 3, max = 14)
    @Schema(description = "국제번호 형식으로 입력해주세요",
            example = "+821012345678",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;
}