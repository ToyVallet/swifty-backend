package com.swifty.bank.server.exception.authentication;

public class StoredAuthValueNotExistException extends AuthenticationException {
    public StoredAuthValueNotExistException(String msg) {
        super(msg);
    }
}
