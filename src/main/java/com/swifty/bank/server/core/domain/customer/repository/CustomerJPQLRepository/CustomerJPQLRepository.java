package com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository;

import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.Customer;
import java.util.Optional;
import java.util.UUID;

public interface CustomerJPQLRepository {
    Optional<Customer> findOneByUUID(UUID uuid);

    Optional<Customer> findOneByDeviceId(String deviceId);

    Optional<Customer> findOneByPhoneNumber(String phoneNumber);

    Optional<CustomerInfoResponse> findCustomerInfoResponseByUUID(UUID uuid);

    void deleteCustomer(Customer customer);
}
