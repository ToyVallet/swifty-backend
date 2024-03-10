package com.swifty.bank.server.exception.authentication;

public class NotLoggedInCustomerException extends AuthenticationException {
    public NotLoggedInCustomerException(String msg) {
        super(msg);
    }
}
