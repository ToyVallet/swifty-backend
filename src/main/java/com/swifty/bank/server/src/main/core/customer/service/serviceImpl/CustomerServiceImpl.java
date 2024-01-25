package com.swifty.bank.server.src.main.core.customer.service.serviceImpl;

import com.swifty.bank.server.src.main.core.customer.Customer;
import com.swifty.bank.server.src.main.core.customer.constant.CustomerStatus;
import com.swifty.bank.server.src.main.core.customer.constant.Nationality;
import com.swifty.bank.server.src.main.core.customer.dto.CustomerJoinDto;
import com.swifty.bank.server.src.main.core.customer.repository.CustomerRepository;
import com.swifty.bank.server.src.main.core.customer.service.CustomerService;
import java.util.Optional;
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
                .id(UUID.randomUUID())
                .name(customerJoinDto.getName())
                .customerStatus(CustomerStatus.ACTIVE)  // 일단 default
                .nationality(customerJoinDto.getNationality())
                .phoneNumber(customerJoinDto.getPhoneNumber())
                .build();

        customerRepository.customSave(customer);
    }
}