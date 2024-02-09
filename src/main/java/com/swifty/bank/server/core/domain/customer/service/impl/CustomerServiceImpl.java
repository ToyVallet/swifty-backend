package com.swifty.bank.server.core.domain.customer.service.impl;

import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

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
                .deviceId(joinRequest.getDeviceId())
                .birthDate(joinRequest.getBirthDate())
                .gender(joinRequest.getGender())
                .roles(joinRequest.getRoles())
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
        if (uuid == null) {
            return null;
        }

        return customerRepository.findOneByUUID(uuid)
                .orElse(null);
    }

    @Override
    public Customer findByDeviceId(String deviceId) {
        if (deviceId == null)
            return null;

        return customerRepository.findOneByDeviceId(deviceId)
                .orElse(null);
    }

    @Override
    public Customer findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        return customerRepository.findOneByPhoneNumber(phoneNumber)
                .orElse(null);
    }

    @Transactional
    @Override
    public Customer updateCustomerInfo(UUID customerUuid, CustomerInfoUpdateConditionRequest customerInfoUpdateConditionRequest) {

        Customer customer = customerRepository.findOneByUUID(customerUuid)
                .orElse(null);

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getName()))
            customer.updateName(customerInfoUpdateConditionRequest.getName());

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getPhoneNumber()))
            customer.updatePhoneNumber(customerInfoUpdateConditionRequest.getPhoneNumber());

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getBirthDate()))
            customer.updateBirthDate(customerInfoUpdateConditionRequest.getBirthDate());

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getNationality()))
            customer.updateNationality(customerInfoUpdateConditionRequest.getNationality());

        return customer;
    }

    @Transactional
    @Override
    public Customer updatePhoneNumber(UUID uuid, String phoneNumber) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElse(null);

        customer.updatePhoneNumber(phoneNumber);
        return customer;
    }

    @Transactional
    @Override
    public Customer updateDeviceId(UUID uuid, String deviceId) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElse(null);

        customer.updateDeviceId(deviceId);
        return customer;
    }

    @Transactional
    @Override
    public Customer withdrawCustomer(UUID uuid) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElse(null);

        if (customer == null) {
            return null;
        }

        customer.delete();
        return customer;
    }

    @Override
    public CustomerInfoResponse findCustomerInfoDtoByUuid(UUID uuid) {
        CustomerInfoResponse customerInfoResponse = customerRepository.findCustomerInfoResponseByUUID(uuid)
                .orElse(null);

        return customerInfoResponse;
    }

    @Override
    public void updatePassword(UUID uuid, String newPassword) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElse(null);

        customer.resetPassword(newPassword);
    }

    @Override
    public boolean isSamePassword(Customer customer, String password) {
        return false;
    }
}