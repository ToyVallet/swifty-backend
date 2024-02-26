package com.swifty.bank.server.core.common.authentication.repository.auth.impl;

import com.swifty.bank.server.core.common.authentication.RefreshToken;
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
    public Optional<RefreshToken> findAuthByUuid(UUID uuid) {
        return em.createQuery(
                        "SELECT A FROM Auth A WHERE A.isDeleted = :isDeleted AND A.id = :uuid", RefreshToken.class
                )
                .setParameter("isDeleted", isDeleted)
                .setParameter("uuid", uuid)
                .getResultList()
                .stream()
                .findAny();
    }
}
