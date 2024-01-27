package com.swifty.bank.server.core.customer.repository.CustomerJPQLRepository;

import com.swifty.bank.server.core.customer.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJPQLRepository {
    Optional<Customer> findOneByUUID(UUID uuid);
    void deleteCustomer(Customer customer);
}
