package com.swifty.bank.server.exception.account;

public class RequestorAndOwnerOfUnitedAccountIsDifferentException extends IllegalArgumentException {
    public RequestorAndOwnerOfUnitedAccountIsDifferentException(String msg) {
        super(msg);
    }
}
