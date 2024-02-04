package com.swifty.bank.server.core.domain.customer.service;

import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.UUID;

public interface CustomerService {
    Customer join(JoinRequest joinRequest);

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL
    Customer findByUuid(UUID uuid);

    Customer findByPhoneNumber(String phoneNumber);

    Customer findByDeviceID(String deviceId);

    Customer updatePhoneNumber(UUID uuid, String phoneNumber);

    Customer updateDeviceID(UUID uuid, String deviceId);
    void withdrawCustomer(UUID uuid);
}