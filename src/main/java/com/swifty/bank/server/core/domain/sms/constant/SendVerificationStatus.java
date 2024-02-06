package com.swifty.bank.server.core.domain.sms.constant;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum SendVerificationStatus {
    PENDING("pending"),
    APPROVED("approved"),
    CANCELED("canceled");

    private final String status;

    SendVerificationStatus(String status) {
        this.status = status;
    }

    public static SendVerificationStatus findByTwilioStatus(String twilioStatus) {
        return Arrays.stream(SendVerificationStatus.values())
                .filter(sendVerificationStatus -> twilioStatus
                        .equals(sendVerificationStatus.getStatus()))
                .findAny()
                .orElse(CANCELED);
    }
}