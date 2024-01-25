package com.swifty.bank.server.src.main.core.customer.repository;

import com.swifty.bank.server.src.main.core.customer.Customer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class CustomCustomerRepositoryImpl implements CustomCustomerRepository{
    private final EntityManager entityManager;

    @Transactional
    public void customSave(Customer customer) {
        entityManager.persist(customer);
    }
}