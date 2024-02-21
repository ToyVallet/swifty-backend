package com.swifty.bank.server.core.common.authentication.repository.custom;

import com.swifty.bank.server.core.common.authentication.ExpiredRefToken;

import java.util.Optional;

public interface ExpiredRefJpqlRepository {
    Optional<ExpiredRefToken> findRefByRefToken(String refToken);
}
