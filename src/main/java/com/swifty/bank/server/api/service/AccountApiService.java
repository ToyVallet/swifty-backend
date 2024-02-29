package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.account.request.*;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AccountApiService {
    public ResponseResult<?> register(String jwt, AccountRegisterRequest req);

    public ResponseResult<?> updateNickname(String jwt, ReviseAccountNicknameRequest req);

    public ResponseResult<?> updatePassword(String jwt, ReviseUnitedAccountPasswordRequest req);

    public ResponseResult<?> retrieveBalanceWithCurrency(String jwt, RetrieveBalanceWithCurrencyRequest req);

    public ResponseResult<?> withdraw(String jwt, WithdrawUnitedAccountRequest req);

    public ResponseResult<?> updateUnitedAccountStatus(String jwt, UpdateUnitedAccountStatusRequest req);

    public ResponseResult<?> updateSubAccountStatus(String jwt, UpdateSubAccountStatusRequest req);

    public ResponseResult<?> updateDefaultCurrency(String jwt, UpdateDefaultCurrencyRequest req);

    public ResponseResult<?> listUnitedAccountWithCustomer(String jwt);
}
