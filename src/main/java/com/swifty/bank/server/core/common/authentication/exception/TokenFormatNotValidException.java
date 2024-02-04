package com.swifty.bank.server.core.common.authentication.exception;

public class TokenFormatNotValidException extends IllegalArgumentException {
    public TokenFormatNotValidException(String msg) {
        super(msg);
    }
}
