package com.swifty.bank.server.core.domain.sms.service.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "format requesting to server to send sms")
public class SendMessageRequest {
    @NotNull
    @Size(max = 14, min = 3)
    @Schema(description = "start with +82 and only digits 0-9 without dash", example = "+8201012345678",
            required = true)
    private String destinationPhoneNumber;
    @Schema(description = "message to be sent to a phone")
    private String smsMessage;
}