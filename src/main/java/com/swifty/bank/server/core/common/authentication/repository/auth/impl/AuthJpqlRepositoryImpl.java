package com.swifty.bank.server.core.common.authentication.repository.auth.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.repository.auth.AuthJpqlRepository;
import com.swifty.bank.server.core.common.redis.entity.RefreshTokenCache;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AuthJpqlRepositoryImpl implements AuthJpqlRepository {
    private final EntityManager em;

    @Override
    public Optional<RefreshTokenCache> findAuthByUuid(UUID uuid) {
        return em.createQuery(
                        "SELECT A FROM RefreshTokenCache A WHERE A.isDeleted = :isDeleted AND A.id = :uuid", RefreshTokenCache.class
                )
                .setParameter("isDeleted", false)
                .setParameter("uuid", uuid)
                .getResultList()
                .stream()
                .findAny();
    }
}
