package com.swifty.bank.server.exception;

public class NotLoggedInCustomerException extends AuthenticationException {
    public NotLoggedInCustomerException(String msg) {
        super(msg);
    }
}
