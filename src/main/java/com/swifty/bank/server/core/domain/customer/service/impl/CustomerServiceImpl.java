package com.swifty.bank.server.core.domain.customer.service.impl;

import com.swifty.bank.server.api.controller.dto.customer.request.CustomerInfoUpdateConditionRequest;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.dto.CustomerInfoDto;
import com.swifty.bank.server.core.domain.customer.dto.JoinDto;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder encoder;

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL

    @Transactional
    @Override
    public Customer join(JoinDto joinDto) {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(joinDto.getName())
                .gender(joinDto.getGender())
                .birthDate(joinDto.getBirthDate())
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(joinDto.getNationality())
                .phoneNumber(joinDto.getPhoneNumber())
                .password(encoder.encode(joinDto.getPassword()))
                .deviceId(joinDto.getDeviceId())
                .roles(joinDto.getRoles())
                .build();

        customerRepository.save(customer);
        return customer;
    }

    @Override
    public Optional<Customer> findByUuid(UUID customerUuid) {

        return customerRepository.findOneByUuid(customerUuid);
    }

    @Override
    public Optional<Customer> findByDeviceId(String deviceId) {

        return customerRepository.findOneByDeviceId(deviceId);
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {

        return customerRepository.findOneByPhoneNumber(phoneNumber);

    }

    @Transactional
    @Override
    public Customer updateCustomerInfo(UUID customerUuid,
                                       CustomerInfoUpdateConditionRequest customerInfoUpdateConditionRequest) {

        Customer customer = customerRepository.findOneByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다"));

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getName())) {
            customer.updateName(customerInfoUpdateConditionRequest.getName());
        }

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getPhoneNumber())) {
            customer.updatePhoneNumber(customerInfoUpdateConditionRequest.getPhoneNumber());
        }

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getBirthDate())) {
            customer.updateBirthDate(customerInfoUpdateConditionRequest.getBirthDate());
        }

        if (Objects.nonNull(customerInfoUpdateConditionRequest.getNationality())) {
            customer.updateNationality(customerInfoUpdateConditionRequest.getNationality());
        }

        return customer;
    }

    @Transactional
    @Override
    public Customer updatePhoneNumber(UUID customerUuid, String phoneNumber) {
        Customer customer = customerRepository.findOneByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] No customer " +
                        "found by the phone" +
                        " number and nationality"));

        customer.updatePhoneNumber(phoneNumber);
        return customer;
    }

    @Transactional
    @Override
    public Customer updateDeviceId(UUID customerUuid, String deviceId) {
        Customer customer = customerRepository.findOneByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] : No customer found by the device id"));

        customer.updateDeviceId(deviceId);
        return customer;
    }

    @Transactional
    @Override
    public void withdrawCustomer(UUID customerUuid) {
        Customer customer = customerRepository.findOneByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("No such Customer"));

        customer.delete();
    }

    @Override
    public Optional<CustomerInfoDto> findCustomerInfoDtoByUuid(UUID customerUuid) {
        return customerRepository.findCustomerInfoResponseByUuid(customerUuid);
    }

    @Transactional
    @Override
    public void updatePassword(UUID customerUuid, String newPassword) {
        String encodePassword = encoder.encode(newPassword);

        Customer customer = customerRepository.findOneByUuid(customerUuid)
                .orElseThrow(() -> new NoSuchElementException("No such Customer"));

        customer.resetPassword(encodePassword);
    }

    @Override
    public boolean isEqualCustomer(Customer customer, String name, String registrationNumber) {
        return customer.getName().equals(name)
                && customer.getGender().equals(extractGender(registrationNumber))
                && customer.getBirthDate()
                .equals(extractBirthDate(registrationNumber));
    }

    @Override
    public Gender extractGender(String residentRegistrationNumber) {
        if (residentRegistrationNumber.endsWith("4") ||
                residentRegistrationNumber.endsWith("2")) {
            return Gender.FEMALE;
        }
        return Gender.MALE;
    }

    @Override
    public String extractBirthDate(String residentRegistrationNumber) {
        return residentRegistrationNumber.substring(0, 6);
    }
}