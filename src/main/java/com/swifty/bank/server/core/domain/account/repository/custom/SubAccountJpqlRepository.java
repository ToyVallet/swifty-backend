package com.swifty.bank.server.core.domain.account.repository.custom;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.SubAccount;
import com.swifty.bank.server.core.domain.account.UnitedAccount;

import java.util.Optional;
import java.util.UUID;

public interface SubAccountJpqlRepository {
    public Optional<SubAccount> findSubAccountByCurrencyAndUnitedAccountUuid(UnitedAccount unitedAccount, Currency currency);
}
