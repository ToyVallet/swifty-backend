package com.swifty.bank.server.core.domain.sms.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request for to send verification code")
public class SendVerificationCodeRequest {
    @Schema(description = "where server send a sms")
    private String phoneNumber;
}
