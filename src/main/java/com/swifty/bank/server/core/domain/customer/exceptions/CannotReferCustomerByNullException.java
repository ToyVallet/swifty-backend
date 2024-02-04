package com.swifty.bank.server.core.domain.customer.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.swifty.bank.server.core.domain.customer.Customer;

public class CannotReferCustomerByNullException extends InvalidFormatException {
    public CannotReferCustomerByNullException( ) {
        super("[ERROR] retrieving customer with null.", null, Customer.class);
    }
}
