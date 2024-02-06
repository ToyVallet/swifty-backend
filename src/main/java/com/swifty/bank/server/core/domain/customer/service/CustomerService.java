package com.swifty.bank.server.core.domain.customer.service;

import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.exceptions.CannotReferCustomerByNullException;

import java.util.UUID;

public interface CustomerService {
    Customer join(JoinRequest joinRequest);

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL
    Customer findByUuid(UUID uuid) throws CannotReferCustomerByNullException;

    Customer findByPhoneNumber(String phoneNumber) throws CannotReferCustomerByNullException;

    Customer findByDeviceId(String deviceId) throws CannotReferCustomerByNullException;

    Customer updatePhoneNumber(UUID uuid, String newPhoneNumber);

    Customer updateDeviceId(UUID uuid, String newDeviceId);

    Customer updateCustomerInfo(UUID customerUuid, CustomerInfoUpdateConditionRequest customerInfoUpdateConditionRequest);

    CustomerInfoResponse findCustomerInfoDtoByUuid(UUID uuid);

    void updatePassword(UUID uuid, String newPassword);

    boolean isSamePassword(Customer customer, String password);
    void withdrawCustomer(UUID uuid);
}