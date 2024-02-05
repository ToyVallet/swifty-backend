package com.swifty.bank.server.core.common.authentication.exception;

public class AuthenticationException extends IllegalArgumentException {
    public AuthenticationException(String msg) {
        super(msg);
    }
}
