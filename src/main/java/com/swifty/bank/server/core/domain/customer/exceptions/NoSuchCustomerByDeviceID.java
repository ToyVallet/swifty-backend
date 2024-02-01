package com.swifty.bank.server.core.domain.customer.exceptions;

import java.util.NoSuchElementException;

public class NoSuchCustomerByDeviceID extends NoSuchElementException {
    public NoSuchCustomerByDeviceID(String message) {
        super(message);
    }
}
