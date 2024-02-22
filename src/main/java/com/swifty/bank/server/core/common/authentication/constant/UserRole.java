package com.swifty.bank.server.core.common.authentication.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {

    USER("유저"),
    ADMIN("어드민");


    private final String role;
}
