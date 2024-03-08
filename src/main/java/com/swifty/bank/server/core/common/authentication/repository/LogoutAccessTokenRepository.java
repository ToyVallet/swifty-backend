package com.swifty.bank.server.core.common.authentication.repository;

import com.swifty.bank.server.core.common.authentication.LogoutAccessToken;
import com.swifty.bank.server.core.common.authentication.repository.logout.LogoutAccessTokenJpqlRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutAccessTokenRepository extends JpaRepository<LogoutAccessToken, String>, LogoutAccessTokenJpqlRepository {
    @Override
    <A extends LogoutAccessToken> A save(A o);
}
