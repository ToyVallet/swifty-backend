package com.swifty.bank.server.core.domain.customer.service;

import com.swifty.bank.server.api.controller.dto.auth.request.JoinRequest;
import com.swifty.bank.server.api.controller.dto.customer.response.CustomerInfoResponse;
import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.Customer;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer join(JoinRequest joinRequest);

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL
    Optional<Customer> findByUuid(UUID uuid);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByDeviceId(String deviceId);

    Customer updatePhoneNumber(UUID uuid, String newPhoneNumber);

    Customer updateDeviceId(UUID uuid, String newDeviceId);

    Customer updateCustomerInfo(UUID customerUuid,
                                CustomerInfoUpdateConditionRequest customerInfoUpdateConditionRequest);

    Optional<CustomerInfoResponse> findCustomerInfoDtoByUuid(UUID uuid);

    void updatePassword(UUID uuid, String newPassword);

    void withdrawCustomer(UUID uuid);
}