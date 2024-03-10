package com.swifty.bank.server.api.controller.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "회원가입/로그인 가능 여부 확인 폼")
public class CheckLoginAvailabilityRequest {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 30)
    @Schema(description = "고객 이름",
            example = "김성명",
            requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 7, max = 7)
    @Pattern(regexp = "[0-9]{2}([0][1-9]|[1][0-2])([0][1-9]|[1-2][0-9]|[3][0-1])[1-4]")
    @Schema(description = "고객 주민등록번호 앞 7자리 (YYMMDDS)",
            example = "0012024",
            requiredMode = RequiredMode.REQUIRED)
    private String residentRegistrationNumber;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 10)
    @Schema(description = "통신사",
            example = "KT",
            requiredMode = RequiredMode.REQUIRED)
    private String mobileCarrier;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 18)
    @Pattern(regexp = "^\\+[0-9]{1,17}")
    @Schema(description = "국제번호 형식으로 입력해주세요.",
            example = "+821012345678",
            requiredMode = RequiredMode.REQUIRED)
    private String phoneNumber;
}