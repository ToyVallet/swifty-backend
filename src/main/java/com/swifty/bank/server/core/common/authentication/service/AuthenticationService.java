package com.swifty.bank.server.core.common.authentication.service;

import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.UUID;

public interface AuthenticationService {
    TokenDto generateTokenDtoWithCustomer(Customer customer);

    Customer logout(UUID uuid);

    boolean isLoggedOut(String key);
}
