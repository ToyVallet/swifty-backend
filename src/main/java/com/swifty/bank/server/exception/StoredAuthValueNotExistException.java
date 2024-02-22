package com.swifty.bank.server.exception;

public class StoredAuthValueNotExistException extends AuthenticationException {
    public StoredAuthValueNotExistException(String msg) {
        super(msg);
    }
}
