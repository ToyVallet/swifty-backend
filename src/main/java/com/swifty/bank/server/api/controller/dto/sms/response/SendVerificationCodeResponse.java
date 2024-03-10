package com.swifty.bank.server.api.controller.dto.sms.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendVerificationCodeResponse {
    @Schema(description = "문자 전송 성공 여부", example = "true")
    private Boolean isSuccess;
}