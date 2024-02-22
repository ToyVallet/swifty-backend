package com.swifty.bank.server.exception;

public class TokenFormatNotValidException extends AuthenticationException {
    public TokenFormatNotValidException(String msg) {
        super(msg);
    }
}
