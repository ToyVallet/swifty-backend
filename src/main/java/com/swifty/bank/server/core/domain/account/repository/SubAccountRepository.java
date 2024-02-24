package com.swifty.bank.server.core.domain.account.repository;

import com.swifty.bank.server.core.domain.account.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubAccountRepository extends JpaRepository<SubAccount, UUID> {
    @Override
    <SA extends SubAccount> SA save(SA entity);
}
