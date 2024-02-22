package com.swifty.bank.server.core.common.authentication.repository.custom.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.repository.custom.AuthJpqlRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class AuthJqplRepositoryImpl implements AuthJpqlRepository {
    private final EntityManager em;
    private final boolean isDeletd = false;

    @Override
    public Optional<Auth> findAuthByUuid(UUID uuid) {
        return em.createQuery(
                        "SELECT A FROM Auth A WHERE A.isDeleted = :isDeletd AND A.uuid = :uuid", Auth.class
                )
                .setParameter("isDeletd", false)
                .setParameter("uuid", uuid)
                .getResultList()
                .stream()
                .findAny();
    }
}
