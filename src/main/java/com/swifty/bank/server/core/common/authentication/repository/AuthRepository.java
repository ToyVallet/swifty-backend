package com.swifty.bank.server.core.common.authentication.repository;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.repository.auth.AuthJpqlRepository;
import java.util.UUID;

import com.swifty.bank.server.core.common.redis.entity.RefreshTokenCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<RefreshTokenCache, UUID>, AuthJpqlRepository {
    @Override
    <A extends RefreshTokenCache> A save(A auth);
}
