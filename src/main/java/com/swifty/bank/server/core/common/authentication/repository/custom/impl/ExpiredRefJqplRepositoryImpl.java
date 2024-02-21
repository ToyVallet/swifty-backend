package com.swifty.bank.server.core.common.authentication.repository.custom.impl;

import com.swifty.bank.server.core.common.authentication.ExpiredRefToken;
import com.swifty.bank.server.core.common.authentication.repository.custom.ExpiredRefJpqlRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ExpiredRefJqplRepositoryImpl implements ExpiredRefJpqlRepository {
    private final EntityManager em;

    @Override
    public Optional<ExpiredRefToken> findRefByRefToken(String refToken) {
        return em.createQuery(
                        "SELECT E FROM ExpiredRefToken E WHERE E.isDeleted = :isDeletd AND E.refreshToken = :refToken",
                        ExpiredRefToken.class
                )
                .getResultList()
                .stream()
                .findAny();
    }
}
