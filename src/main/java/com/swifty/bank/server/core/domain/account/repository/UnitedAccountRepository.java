package com.swifty.bank.server.core.domain.account.repository;

import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.repository.custom.UnitedAccountJpqlRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UnitedAccountRepository extends JpaRepository<UnitedAccount, UUID>, UnitedAccountJpqlRepository {
    @Override
    <UA extends UnitedAccount> UA save(UA entity);
}
