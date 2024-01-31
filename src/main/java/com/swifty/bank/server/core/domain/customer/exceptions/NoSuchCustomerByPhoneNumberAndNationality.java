package com.swifty.bank.server.core.domain.customer.exceptions;

import java.util.NoSuchElementException;

public class NoSuchCustomerByPhoneNumberAndNationality extends NoSuchElementException {
    public NoSuchCustomerByPhoneNumberAndNationality(String message) {
        super(message);
    }
}
