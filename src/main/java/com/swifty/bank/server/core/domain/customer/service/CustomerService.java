package com.swifty.bank.server.core.domain.customer.service;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoDto;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer join(JoinDto joinDto);

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL
    Optional<Customer> findByUuid(UUID customerUuid);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByDeviceId(String deviceId);

    Customer updatePhoneNumber(UUID customerUuid, String newPhoneNumber);

    Customer updateDeviceId(UUID customerUuid, String newDeviceId);

    Customer updateCustomerInfo(UUID customerUuid,
                                CustomerInfoUpdateConditionRequest customerInfoUpdateConditionRequest);

    Optional<CustomerInfoDto> findCustomerInfoDtoByUuid(UUID customerUuid);

    void updatePassword(UUID customerUuid, String newPassword);

    void withdrawCustomer(UUID customerUuid);

    boolean isEqualCustomer(Customer customer, String name, String registrationNumber);

    Gender extractGender(String residentRegistrationNumber);

    String extractBirthDate(String residentRegistrationNumber);
}