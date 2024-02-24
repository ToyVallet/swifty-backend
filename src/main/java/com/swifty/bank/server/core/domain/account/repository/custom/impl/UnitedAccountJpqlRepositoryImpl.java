package com.swifty.bank.server.core.domain.account.repository.custom.impl;

import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.repository.custom.UnitedAccountJpqlRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UnitedAccountJpqlRepositoryImpl implements UnitedAccountJpqlRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public Optional<UnitedAccount> findByUuid(UUID uuid) {
        return em.createQuery(
                        "SELECT UA FROM UnitedAccount UA WHERE isDeleted = :isDeleted AND unitedAccountUuid = :uuid",
                        UnitedAccount.class
                )
                .setParameter("isDeleted", isDeleted)
                .setParameter("uuid", uuid)
                .getResultList()
                .stream()
                .findAny();
    }
}
