package com.swifty.bank.server.core.common.redis.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TemporarySignUpForm {
    private String name;
    private String residentRegistrationNumber;
    private String mobileCarrier;
    private String phoneNumber;
}