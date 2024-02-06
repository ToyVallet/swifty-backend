package com.swifty.bank.server.core.common.authentication.exception;

public class TokenContentNotValidException extends AuthenticationException {
    public TokenContentNotValidException(String msg) {
        super(msg);
    }
}
