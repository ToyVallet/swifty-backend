package com.swifty.bank.server.exception.authentication;

public class NoSuchAuthByUuidException extends AuthenticationException {
    public NoSuchAuthByUuidException(String msg) {
        super(msg);
    }
}
