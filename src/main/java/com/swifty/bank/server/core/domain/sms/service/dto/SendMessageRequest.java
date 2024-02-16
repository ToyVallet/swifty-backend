package com.swifty.bank.server.core.domain.sms.service.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "format requesting to server to send sms")
public class SendMessageRequest {
    @Schema(description = "phone number to send a message")
    private String destinationPhoneNumber;
    @Schema(description = "message to be sent to a phone")
    private String smsMessage;
}