package com.swifty.bank.server.core.common.authentication.exception;

public class NoSuchAuthByUuidException extends AuthenticationException {
    public NoSuchAuthByUuidException(String msg) {
        super(msg);
    }
}
