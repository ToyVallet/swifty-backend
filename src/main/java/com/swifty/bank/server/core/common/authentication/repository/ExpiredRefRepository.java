package com.swifty.bank.server.core.common.authentication.repository;

import com.swifty.bank.server.core.common.authentication.ExpiredRefToken;
import com.swifty.bank.server.core.common.authentication.repository.custom.ExpiredRefJpqlRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpiredRefRepository extends JpaRepository<ExpiredRefToken, String>, ExpiredRefJpqlRepository {
    @Override
    <E extends ExpiredRefToken> E save(E expiredRefToken);
}
