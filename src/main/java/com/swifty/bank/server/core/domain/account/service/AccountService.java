package com.swifty.bank.server.core.domain.account.service;

import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.dto.AccountNicknameUpdateDto;
import com.swifty.bank.server.core.domain.account.dto.AccountPasswordUpdateDto;
import com.swifty.bank.server.core.domain.account.dto.AccountSaveDto;
import com.swifty.bank.server.core.domain.account.dto.RetrieveBalanceOfUnitedAccountByCurrencyDto;

public interface AccountService {
    public UnitedAccount saveMultipleCurrencyAccount(AccountSaveDto dto);

    public void updateUaNickname(AccountNicknameUpdateDto nickname);

    public void updateUaPassword(AccountPasswordUpdateDto password);

    public double retrieveBalanceByCurrency(RetrieveBalanceOfUnitedAccountByCurrencyDto dto);
}
