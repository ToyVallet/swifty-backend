package com.swifty.bank.server.exception;

public class AuthenticationException extends IllegalArgumentException {
    public AuthenticationException(String msg) {
        super(msg);
    }
}
