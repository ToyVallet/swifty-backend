package com.swifty.bank.server.core.domain.account.exception;

import java.util.NoSuchElementException;

public class NoSuchUnitedAccountByUuidException extends NoSuchElementException {
    public NoSuchUnitedAccountByUuidException(String msg) {
        super(msg);
    }
}
