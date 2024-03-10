package com.swifty.bank.server.core.common.authentication.repository.auth.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.repository.auth.AuthJpqlRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AuthJpqlRepositoryImpl implements AuthJpqlRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public Optional<Auth> findAuthByUuid(UUID uuid) {
        return em.createQuery(
                        "SELECT A FROM Auth A WHERE A.isDeleted = :isDeleted AND A.id = :uuid", Auth.class
                )
                .setParameter("isDeleted", isDeleted)
                .setParameter("uuid", uuid)
                .getResultList()
                .stream()
                .findAny();
    }
}
