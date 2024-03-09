package com.swifty.bank.server.api.controller.dto.sms.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StealVerificationCodeResponse {
    @Schema(description = "생성된 인증번호",
            example = "123456")
    private String otp;
}