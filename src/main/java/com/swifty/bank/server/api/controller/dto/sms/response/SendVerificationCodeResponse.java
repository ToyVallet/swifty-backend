package com.swifty.bank.server.api.controller.dto.sms.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendVerificationCodeResponse {
    @Schema(example = "인증번호 전송 성공")
    private String message;
}