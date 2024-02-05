package com.swifty.bank.server.core.common.authentication.service;

import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.domain.customer.Customer;

public interface AuthenticationService {
    TokenDto generateTokenWithCustomer(Customer customer);
}
