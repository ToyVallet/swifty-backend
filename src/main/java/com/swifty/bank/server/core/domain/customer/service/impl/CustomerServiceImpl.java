package com.swifty.bank.server.core.domain.customer.service.impl;

import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoResponse;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.domain.customer.exceptions.CannotReferCustomerByNullException;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByDeviceID;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByPhoneNumberException;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByUUID;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
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
    public Customer findByUuid(UUID uuid) throws CannotReferCustomerByNullException {
        if (uuid == null) {
            throw new CannotReferCustomerByNullException();
        }

        return customerRepository.findOneByUUID(uuid)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
    }

    @Override
    public Customer findByDeviceId(String deviceId) throws CannotReferCustomerByNullException {
        if (deviceId == null)
            throw new CannotReferCustomerByNullException();

        return customerRepository.findOneByDeviceId(deviceId)
                .orElseThrow(() -> new NoSuchCustomerByDeviceID("[ERROR] No result as referring with device ID"));
    }

    @Override
    public Customer findByPhoneNumber(String phoneNumber) throws CannotReferCustomerByNullException {
        if (phoneNumber == null) {
            throw new CannotReferCustomerByNullException();
        }

        return customerRepository.findOneByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new NoSuchCustomerByPhoneNumberException("[ERROR] No Result as referring with phone " +
                                "number and nationality")
                );
    }

    @Transactional
    @Override
    public Customer updateCustomerInfo(UUID customerUuid, CustomerInfoUpdateConditionRequest customerInfoUpdateConditionRequest) {

        Customer customer = customerRepository.findOneByUUID(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다"));

        if(Objects.nonNull(customerInfoUpdateConditionRequest.getName())) customer.updateName(customerInfoUpdateConditionRequest.getName());

        if(Objects.nonNull(customerInfoUpdateConditionRequest.getPhoneNumber())) customer.updatePhoneNumber(customerInfoUpdateConditionRequest.getPhoneNumber());

        if(Objects.nonNull(customerInfoUpdateConditionRequest.getBirthDate())) customer.updateBirthDate(customerInfoUpdateConditionRequest.getBirthDate());

        if(Objects.nonNull(customerInfoUpdateConditionRequest.getNationality())) customer.updateNationality(customerInfoUpdateConditionRequest.getNationality());

        return customer;
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
    public Customer updateDeviceId(UUID uuid, String deviceId) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElseThrow(() -> new NoSuchCustomerByUUID("[ERROR] : No customer found by the device id"));

        customer.updateDeviceId(deviceId);
        return customer;
    }

    @Transactional
    @Override
    public void withdrawCustomer(UUID uuid) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElseThrow(() -> new NoSuchCustomerByUUID("No such Customer"));

        customer.delete();
    }

    @Override
    public CustomerInfoResponse findCustomerInfoDtoByUuid(UUID uuid) {
        CustomerInfoResponse customerInfoResponse = customerRepository.findCustomerInfoResponseByUUID(uuid)
                .orElseThrow(() -> new NoSuchElementException("No such Customer"));

        return customerInfoResponse;
    }

    @Override
    public void updatePassword(UUID uuid, String newPassword) {
        Customer customer = customerRepository.findOneByUUID(uuid)
                .orElseThrow(() -> new NoSuchElementException("No such Customer"));

        customer.resetPassword(newPassword);
    }

    @Override
    public boolean isSamePassword(Customer customer, String password) {
        return false;
    }
}