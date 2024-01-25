package com.swifty.bank.server.src.main.core.config;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class jpaEntityManagerBeanConfig {
    private EntityManager entityManager;

    public jpaEntityManagerBeanConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}