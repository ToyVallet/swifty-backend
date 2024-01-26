package com.swifty.bank.server.core.config;

import com.swifty.bank.server.core.customer.repository.CustomerJPQLRepository.CustomerJPQLRepository;
import com.swifty.bank.server.core.customer.repository.CustomerJPQLRepository.JPQLImpl.CustomerJPQLRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class jpaEntityManagerBeanConfig {
    private EntityManager entityManager;

    public jpaEntityManagerBeanConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    public CustomerJPQLRepositoryImpl customerJPQLRepository(EntityManager entityManager) {
       return new CustomerJPQLRepositoryImpl(entityManager);
    }
}