package com.swifty.bank.server.core.domain.account.exception;

public class RequestorAndOwnerOfUnitedAccountIsDifferentException extends IllegalArgumentException {
    public RequestorAndOwnerOfUnitedAccountIsDifferentException(String msg) {
        super(msg);
    }
}
