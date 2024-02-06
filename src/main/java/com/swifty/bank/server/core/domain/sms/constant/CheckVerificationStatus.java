package com.swifty.bank.server.core.domain.sms.constant;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CheckVerificationStatus {
    PENDING("pending"),
    APPROVED("approved"),
    CANCELED("canceled");

    private final String status;

    CheckVerificationStatus(String status) {
        this.status = status;
    }

    public static CheckVerificationStatus findByTwilioStatus(String twilioStatus) {
        return Arrays.stream(CheckVerificationStatus.values())
                .filter(checkVerificationStatus -> twilioStatus
                        .equals(checkVerificationStatus.getStatus()))
                .findAny()
                .orElse(CANCELED);
    }
}