package com.swifty.bank.server.exception.authentication;

public class TokenNotExistException extends AuthenticationException {
    public TokenNotExistException(String msg) {
        super(msg);
    }
}