package com.swifty.bank.server.exception.authentication;

public class TokenContentNotValidException extends AuthenticationException {
    public TokenContentNotValidException(String msg) {
        super(msg);
    }
}
