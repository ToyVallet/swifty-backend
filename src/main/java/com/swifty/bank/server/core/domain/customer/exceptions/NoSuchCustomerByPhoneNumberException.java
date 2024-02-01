package com.swifty.bank.server.core.domain.customer.exceptions;

import java.util.NoSuchElementException;

public class NoSuchCustomerByPhoneNumberException extends NoSuchElementException {
    public NoSuchCustomerByPhoneNumberException(String message) {
        super(message);
    }
}
