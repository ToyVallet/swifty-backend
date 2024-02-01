package com.swifty.bank.server.core.domain.customer.exceptions;

import java.util.NoSuchElementException;

public class NoSuchCustomerByUUID extends NoSuchElementException {
    public NoSuchCustomerByUUID(String msg) {
        super(msg);
    }
}
