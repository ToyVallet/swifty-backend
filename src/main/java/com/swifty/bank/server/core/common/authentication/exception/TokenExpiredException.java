package com.swifty.bank.server.core.common.authentication.exception;

public class TokenExpiredException extends AuthenticationException {
    public TokenExpiredException(String msg) {
        super(msg);
    }
}
