package com.swifty.bank.server.api.service.dto;

public enum Result {
    SUCCESS("성공"),
    FAIL("실패");

    private final String description;

    Result(String description) {
        this.description = description;
    }
}