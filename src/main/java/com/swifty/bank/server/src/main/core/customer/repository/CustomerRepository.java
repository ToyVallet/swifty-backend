package com.swifty.bank.server.src.main.core.customer.repository;

import com.swifty.bank.server.src.main.core.customer.Customer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, CustomCustomerRepository{
    @Override
    <S extends Customer> S save(S entity);
}