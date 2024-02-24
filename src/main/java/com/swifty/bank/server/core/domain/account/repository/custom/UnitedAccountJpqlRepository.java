package com.swifty.bank.server.core.domain.account.repository.custom;

import com.swifty.bank.server.core.domain.account.UnitedAccount;

import java.util.Optional;
import java.util.UUID;

public interface UnitedAccountJpqlRepository {
    public Optional<UnitedAccount> findByUuid(UUID uuid);
}
