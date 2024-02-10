package com.swifty.bank.server.core.domain.customer.constant;

public enum Gender {
    MALE("남"),
    FEMALE("여"),
    NONE("성별 선택 안함");
    private final String description;

    Gender(String description) {
        this.description = description;
    }
}
