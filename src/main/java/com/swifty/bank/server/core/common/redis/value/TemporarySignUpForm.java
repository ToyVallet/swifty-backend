package com.swifty.bank.server.core.common.redis.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TemporarySignUpForm {
    private String name;
    private String residentRegistrationNumber;
    private String MobileCarrier;
    private String phoneNumber;
}