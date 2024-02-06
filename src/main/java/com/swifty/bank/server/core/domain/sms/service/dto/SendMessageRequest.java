package com.swifty.bank.server.core.domain.sms.service.dto;


import lombok.Data;

@Data
public class SendMessageRequest {
    private String destinationPhoneNumber;
    private String smsMessage;
}