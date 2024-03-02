package com.swifty.bank.server.api.controller.dto.sms.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckVerificationCodeResponse {
    @Schema(example = "인증번호 검증 성공")
    private String message;
}