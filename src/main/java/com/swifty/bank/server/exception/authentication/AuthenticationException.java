package com.swifty.bank.server.exception.authentication;

public class AuthenticationException extends IllegalArgumentException {
    public AuthenticationException(String msg) {
        super(msg);
    }
}
