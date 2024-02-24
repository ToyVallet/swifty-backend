package com.swifty.bank.server.core.common.authentication.repository.auth;

import com.swifty.bank.server.core.common.authentication.Auth;
import java.util.Optional;
import java.util.UUID;

public interface AuthJpqlRepository {
    Optional<Auth> findAuthByUuid(UUID uuid);
}
