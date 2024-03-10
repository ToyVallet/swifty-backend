package com.swifty.bank.server.api.controller.dto.sms.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckVerificationCodeResponse {
    @Schema(description = "인증번호 검증 성공 여부",
            example = "true")
    private Boolean isSuccess;
}