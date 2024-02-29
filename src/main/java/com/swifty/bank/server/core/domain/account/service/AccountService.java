package com.swifty.bank.server.core.domain.account.service;

import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.dto.*;

import java.util.List;

public interface AccountService {
    public UnitedAccount saveUnitedAccountAndSubAccounts(AccountSaveDto dto);

    public void updateUnitedAccountNickname(AccountNicknameUpdateDto nickname);

    public void updateUnitedAccountPassword(AccountPasswordUpdateDto password);

    public double retrieveBalanceByCurrency(RetrieveBalanceOfUnitedAccountByCurrencyDto dto);

    public void withdrawUnitedAccount(WithdrawUnitedAccountDto dto);

    public void updateUnitedAccountStatus(UpdateUnitedAccountStatusDto dto);

    public void updateSubAccountStatus(UpdateSubAccountStatusDto dto);

    public void updateDefaultCurrency(UpdateDefaultCurrencyDto dto);

    public List<UnitedAccount> listUnitedAccountWithCustomer(ListUnitedAccountWithCustomerDto dto);
}
