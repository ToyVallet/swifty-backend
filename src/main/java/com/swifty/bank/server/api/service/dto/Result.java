package com.swifty.bank.server.api.service.dto;

public enum Result {

    SUCCESS(200),
    FAIL(400);

    private final int code;

    Result(int code) {
        this.code = code;
    }
}
