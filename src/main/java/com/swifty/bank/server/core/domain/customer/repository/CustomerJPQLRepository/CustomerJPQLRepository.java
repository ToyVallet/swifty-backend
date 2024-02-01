package com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJPQLRepository {
    Optional<Customer> findOneByUUID(UUID uuid);
    Optional<Customer> findOneByDeviceID(String deviceID);
    Optional<Customer> findOneByPhoneNumber(String deviceID);
    void deleteCustomer(Customer customer);
}
