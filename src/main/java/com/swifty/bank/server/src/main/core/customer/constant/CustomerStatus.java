package com.swifty.bank.server.src.main.core.customer.constant;

public enum CustomerStatus {
    ACTIVE("나 살아있다"),
    SUSPENDED("정지된 상태"),
    WITHDRAWAL("탈퇴된 상태");

    private final String description;
    CustomerStatus(String description) {
        this.description = description;
    }
}