package com.swifty.bank.server.exception;

public class TokenNotExistException extends AuthenticationException {
    public TokenNotExistException(String msg) {
        super(msg);
    }
}