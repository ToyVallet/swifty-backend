package com.swifty.bank.server.core.domain.customer.repository;

import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.UUID;

import com.swifty.bank.server.core.domain.customer.repository.CustomerJPQLRepository.CustomerJPQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, CustomerJPQLRepository {
    @Override
    <S extends Customer> S save(S entity);
}