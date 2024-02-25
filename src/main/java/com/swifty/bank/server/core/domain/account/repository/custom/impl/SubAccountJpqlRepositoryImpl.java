package com.swifty.bank.server.core.domain.account.repository.custom.impl;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.SubAccount;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.repository.custom.SubAccountJpqlRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SubAccountJpqlRepositoryImpl implements SubAccountJpqlRepository {
    private final EntityManager em;
    private final boolean isDeleted = false;

    @Override
    public Optional<SubAccount> findSubAccountByCurrencyAndUnitedAccountUuid(UnitedAccount unitedAccount, Currency currency) {
        return em.createQuery(
                "SELECT SA FROM SubAccount SA WHERE isDeleted = :isDeleted AND currency = :currency " +
                        "AND unitedAccount = :unitedAccountUuid", SubAccount.class
        )
                .setParameter("unitedAccountUuid", unitedAccount)
                .setParameter("isDeleted", isDeleted)
                .setParameter("currency", currency)
                .getResultList()
                .stream()
                .findAny( );
    }
}
