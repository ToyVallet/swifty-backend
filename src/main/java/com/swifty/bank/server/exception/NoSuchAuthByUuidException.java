package com.swifty.bank.server.exception;

public class NoSuchAuthByUuidException extends AuthenticationException {
    public NoSuchAuthByUuidException(String msg) {
        super(msg);
    }
}
