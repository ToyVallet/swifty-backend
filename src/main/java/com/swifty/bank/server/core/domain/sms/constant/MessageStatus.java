package com.swifty.bank.server.core.domain.sms.constant;

import com.twilio.rest.api.v2010.account.Message.Status;
import java.util.Arrays;

public enum MessageStatus {
    QUEUED("queued"),
    SENDING("sending"),
    SENT("sent"),
    FAILED("failed");

    private final String status;

    private MessageStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static MessageStatus findByTwilioMessageStatus(Status twilioMessageStatus) {
        return Arrays.stream(MessageStatus.values())
                .filter(messageStatus -> twilioMessageStatus.toString().equals(messageStatus.getStatus()))
                .findAny()
                .orElse(FAILED);
    }
}