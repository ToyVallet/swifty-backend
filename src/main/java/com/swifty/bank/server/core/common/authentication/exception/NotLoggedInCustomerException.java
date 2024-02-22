package com.swifty.bank.server.core.common.authentication.exception;

public class NotLoggedInCustomerException extends AuthenticationException {
    public NotLoggedInCustomerException(String msg) {
        super(msg);
    }
}
