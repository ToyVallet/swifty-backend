package com.swifty.bank.server.core.domain.sms.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Form to get verification code")
public class CheckVerificationCodeRequest {
    @Schema(description = "phone's number which be sent with verification code")
    private String phoneNumber;
    @Schema(description = "verification which phone's owner got from server")
    private String verificationCode;
}
