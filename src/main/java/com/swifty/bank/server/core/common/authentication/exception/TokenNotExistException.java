package com.swifty.bank.server.core.common.authentication.exception;

public class TokenNotExistException extends AuthenticationException {
    public TokenNotExistException(String msg) {
        super(msg);
    }
}