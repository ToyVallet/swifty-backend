package com.swifty.bank.server.core.domain.customer.service.impl;

import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByDeviceID;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByPhoneNumberException;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByUUID;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;

import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public Customer join(JoinRequest joinRequest) {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(joinRequest.getName())
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(joinRequest.getNationality())
                .phoneNumber(joinRequest.getPhoneNumber())
                .password(joinRequest.getPassword())
                .deviceID(joinRequest.getDeviceID())
                .build();

        customerRepository.save(customer);
        return customer;
    }

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL
    @Override
    public Customer findByUuid(UUID uuid) {
        return customerRepository.findOneByUUID(uuid)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
    }

    @Override
    public Customer findByDeviceID(String deviceId) {
        return customerRepository.findOneByDeviceID(deviceId)
                .orElseThrow(( ) -> new NoSuchCustomerByDeviceID("[ERROR] No result as referring with device ID"));
    }

    @Override
    public Customer findByPhoneNumber(String phoneNumber) {
        return customerRepository.findOneByPhoneNumber(phoneNumber)
                .orElseThrow(( ) ->
                        new NoSuchCustomerByPhoneNumberException("[ERROR] No Result as referring with phone " +
                                "number and nationality")
                );
    }

    @Transactional
    @Override
    public Customer updatePhoneNumber(UUID uuid, String phoneNumber) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElseThrow(() -> new NoSuchCustomerByPhoneNumberException("[ERROR] No customer " +
                        "found by the phone" +
                        " number and nationality"));

        customer.updatePhoneNumber(phoneNumber);
        return customer;
    }

    @Transactional
    @Override
    public Customer updateDeviceID(UUID uuid, String deviceId) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElseThrow(( ) -> new NoSuchCustomerByUUID("[ERROR] : No customer found by the device id"));

        customer.updateDeviceID(deviceId);
        return customer;
    }

    @Transactional
    @Override
    public void withdrawCustomer(UUID uuid) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElseThrow(() -> new NoSuchElementException("No such Customer"));

        customerRepository.deleteCustomer(customer);
    }
}