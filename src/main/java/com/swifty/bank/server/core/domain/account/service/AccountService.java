package com.swifty.bank.server.core.domain.account.service;

import com.swifty.bank.server.api.controller.dto.account.request.WithdrawUnitedAccountRequest;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import com.swifty.bank.server.core.domain.account.dto.*;

public interface AccountService {
    public UnitedAccount saveMultipleCurrencyAccount(AccountSaveDto dto);

    public void updateUaNickname(AccountNicknameUpdateDto nickname);

    public void updateUaPassword(AccountPasswordUpdateDto password);

    public double retrieveBalanceByCurrency(RetrieveBalanceOfUnitedAccountByCurrencyDto dto);

    public void withdrawUnitedAccount(WithdrawUnitedAccountDto dto);

    public void updateUnitedAccountStatus(UpdateUnitedAccountStatusDto dto);

    public void updateSubAccountStatus(UpdateSubAccountStatusDto dto);
}
