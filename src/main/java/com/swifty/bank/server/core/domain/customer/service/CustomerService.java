package com.swifty.bank.server.core.domain.customer.service;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.*;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerService {
    Customer join(CustomerJoinDto customerJoinDto);

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL
    Customer findById(CustomerFindDto uuid);

    Customer findByPhoneNumber(CustomerFindByPhoneNumberDto dto);

    Customer findByDeviceID(CustomerFindByDeviceIDDto dto);

    Customer updatePhoneNumber(CustomerUpdatePhoneNumberDto dto);

    Customer updateDeviceID(CustomerUpdateDeviceIDDto dto);
    void withdrawCustomer(CustomerFindDto uuid);
}