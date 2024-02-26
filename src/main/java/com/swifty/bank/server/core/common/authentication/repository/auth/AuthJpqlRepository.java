package com.swifty.bank.server.core.common.authentication.repository.auth;

import com.swifty.bank.server.core.common.authentication.RefreshTokenDb;

import java.util.Optional;
import java.util.UUID;

public interface AuthJpqlRepository {
    Optional<RefreshTokenDb> findAuthByUuid(UUID uuid);
}
