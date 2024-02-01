package com.swifty.bank.server.core.domain.customer.service.impl;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.dto.*;
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
    public Customer join(CustomerJoinDto customerJoinDto) {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(customerJoinDto.getName())
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(customerJoinDto.getNationality())
                .phoneNumber(customerJoinDto.getPhoneNumber())
                .password(customerJoinDto.getPassword())
                .deviceID(customerJoinDto.getDeviceID())
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
    public Customer findById(CustomerFindDto uuid) {
        return customerRepository.findOneByUUID(uuid.getUuid())
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
    }

    @Override
    public Customer findByDeviceID(CustomerFindByDeviceIDDto dto) {
        return customerRepository.findOneByDeviceID(dto.getDeviceID( ))
                .orElseThrow(( ) -> new NoSuchCustomerByDeviceID("[ERROR] No result as referring with device ID"));
    }

    @Override
    public Customer findByPhoneNumber(CustomerFindByPhoneNumberDto dto) {
        return customerRepository.findOneByPhoneNumber(dto.getPhoneNumber( ))
                .orElseThrow(( ) ->
                        new NoSuchCustomerByPhoneNumberException("[ERROR] No Result as referring with phone " +
                                "number and nationality")
                );
    }

    @Transactional
    @Override
    public Customer updatePhoneNumber(CustomerUpdatePhoneNumberDto dto) {
        Customer customer = customerRepository.findOneByUUID(dto.getUuid())
                .orElseThrow(() -> new NoSuchCustomerByPhoneNumberException("[ERROR] No customer " +
                        "found by the phone" +
                        " number and nationality"));

        customer.updatePhoneNumber(dto.getPhoneNumber( ));
        return customer;
    }

    @Transactional
    @Override
    public Customer updateDeviceID(CustomerUpdateDeviceIDDto dto) {
        Customer customer = customerRepository.findOneByUUID(dto.getUuid( ))
                .orElseThrow(( ) -> new NoSuchCustomerByUUID("[ERROR] : No customer found by the device id"));

        customer.updateDeviceID(dto.getDeviceID( ));
        return customer;
    }

    @Transactional
    @Override
    public void withdrawCustomer(CustomerFindDto uuid) {
        Customer customer = customerRepository.findOneByUUID(uuid.getUuid())
                .orElseThrow(() -> new NoSuchElementException("No such Customer"));

        customerRepository.deleteCustomer(customer);
    }
}