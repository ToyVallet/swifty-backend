package com.swifty.bank.server.core.domain.account.repository.custom;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;

public interface SubAccountJpqlRepository {
    void depositBalance(Currency currency, double cost);

    void withdrawBalance(Currency currency, double cost);

    double checkBalanceWByCurrency(Currency currency);

    void closeAccountWithCurrency(Currency currency);

    void changeAccountState(Currency currency, AccountStatus status);
}
