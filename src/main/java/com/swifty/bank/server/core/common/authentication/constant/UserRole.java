package com.swifty.bank.server.core.common.authentication.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {

    CUSTOMER("유저"),
    ADMIN("어드민");


    private final String role;
}
