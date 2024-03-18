package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.account.request.*;
import com.swifty.bank.server.api.controller.dto.account.response.*;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AccountApiService {
    public AccountRegisterResponse register(String jwt, AccountRegisterRequest req);

    public UpdateAccountNicknameResponse updateNickname(String jwt, ReviseAccountNicknameRequest req);

    public ReviseUnitedAccountPasswordResponse updatePassword(String jwt, ReviseUnitedAccountPasswordRequest req);

    public RetrieveBalanceWithCurrencyResponse retrieveBalanceWithCurrency(String jwt, RetrieveBalanceWithCurrencyRequest req);

    public WithdrawUnitedAccountResponse withdraw(String jwt, WithdrawUnitedAccountRequest req);

    public UpdateUnitedAccountStatusResponse updateUnitedAccountStatus(String jwt, UpdateUnitedAccountStatusRequest req);

    public UpdateSubAccountStatusResponse updateSubAccountStatus(String jwt, UpdateSubAccountStatusRequest req);

    public UpdateDefaultCurrencyResponse updateDefaultCurrency(String jwt, UpdateDefaultCurrencyRequest req);

    public ListUnitedAccountWithCustomerResponse listUnitedAccountWithCustomer(String jwt);
}
