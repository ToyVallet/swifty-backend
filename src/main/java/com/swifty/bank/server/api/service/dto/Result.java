package com.swifty.bank.server.api.service.dto;

import lombok.Getter;

@Getter
public enum Result {

    SUCCESS(200),
    FAIL(400);

    private final int code;

    Result(int code) {
        this.code = code;
    }
    public boolean equals(Result result) {
        return result.getCode() == this.code;
    }
}
