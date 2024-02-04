package com.swifty.bank.server.core.domain.sms.service.impl;

import com.swifty.bank.server.core.domain.sms.constant.MessageStatus;
import com.swifty.bank.server.core.domain.sms.service.MessageService;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    @Value("${TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Value("${TWILIO_OUTGOING_SMS_NUMBER}")
    private String OUTGOING_SMS_NUMBER;

    @PostConstruct
    private void setup() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
    public MessageStatus sendMessage(String destinationNumber, String smsMessage) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(destinationNumber),
                    new PhoneNumber(OUTGOING_SMS_NUMBER),
                    smsMessage
            ).create();

            return MessageStatus.findByTwilioMessageStatus(message.getStatus());
        } catch (ApiException e) {
            return MessageStatus.FAILED;
        }
    }
}