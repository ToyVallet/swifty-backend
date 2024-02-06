package com.swifty.bank.server.core.domain.customer.constant;

public enum Gender {
    MALE("남"),
    FEMALE("여");
    private final String description;

    Gender(String description) {
        this.description = description;
    }
}
