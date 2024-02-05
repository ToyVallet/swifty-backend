package com.swifty.bank.server.core.common.authentication.exception;

public class StoredAuthValueNotExistException extends AuthenticationException {
    public StoredAuthValueNotExistException(String msg) {
        super(msg);
    }
}
