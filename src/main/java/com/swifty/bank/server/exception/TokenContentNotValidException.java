package com.swifty.bank.server.exception;

public class TokenContentNotValidException extends AuthenticationException {
    public TokenContentNotValidException(String msg) {
        super(msg);
    }
}
