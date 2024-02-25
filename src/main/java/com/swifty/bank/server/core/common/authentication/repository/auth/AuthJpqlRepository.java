package com.swifty.bank.server.core.common.authentication.repository.auth;

import com.swifty.bank.server.core.common.redis.entity.RefreshTokenCache;

import java.util.Optional;
import java.util.UUID;

public interface AuthJpqlRepository {
    Optional<RefreshTokenCache> findAuthByUuid(UUID uuid);
}
