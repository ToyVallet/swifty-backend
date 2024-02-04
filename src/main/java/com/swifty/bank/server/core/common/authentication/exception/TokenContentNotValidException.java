package com.swifty.bank.server.core.common.authentication.exception;

public class TokenContentNotValidException extends IllegalArgumentException {
    public TokenContentNotValidException(String msg) {
        super(msg);
    }
}
