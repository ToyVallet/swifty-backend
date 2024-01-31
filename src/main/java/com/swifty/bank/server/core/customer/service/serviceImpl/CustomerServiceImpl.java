package com.swifty.bank.server.core.customer.service.serviceImpl;

import com.swifty.bank.server.core.customer.Customer;
import com.swifty.bank.server.core.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.customer.dto.CustomerFindDto;
import com.swifty.bank.server.core.customer.dto.CustomerJoinDto;
import com.swifty.bank.server.core.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.customer.service.CustomerService;

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
    public void join(CustomerJoinDto customerJoinDto) {
        Customer customer = Customer.builder()
                .name(customerJoinDto.getName())
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(customerJoinDto.getNationality())
                .phoneNumber(customerJoinDto.getPhoneNumber())
                .sex(customerJoinDto.getSex())
                .bod(customerJoinDto.getBod())
                .deviceId(customerJoinDto.getDeviceId())
                .password(customerJoinDto.getPassword())
                .build();

        customerRepository.save(customer);
    }

    // 순수 UUID로의 조회를 1원칙으로 삼는다
    // Principle 1. Retrieve with User's own UUID (PK)
    // Something to exchange with Frontend as user identification
    // Send access token(JWT) to frontend with encrypted UUID
    // Condition of Retrieval : JPQL
    @Override
    public Customer find(CustomerFindDto uuid) {
        return customerRepository.findOneByUUID(uuid.getUuid())
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
    }

//    @Transactional
//    @Override
//    public Customer updatePhoneNumber(CustomerJoinDto customerJoinDto) {
//        Customer customer = customerRepository.findOneByUUID(customerDto.getUuid())
//                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
//
//        customer.updatePhoneNumber(customerJoinDto.getPhoneNumber());
//        return customer;
//    }

    @Transactional
    @Override
    public void withdrawCustomer(CustomerFindDto uuid) {
        Customer customer = customerRepository.findOneByUUID(uuid.getUuid())
                .orElseThrow(() -> new NoSuchElementException("No such Customer"));

        customerRepository.deleteCustomer(customer);
    }
}