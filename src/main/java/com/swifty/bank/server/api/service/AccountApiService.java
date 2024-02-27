package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.account.request.*;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AccountApiService {
    public ResponseResult<?> registerUnitedAccount(String jwt, AccountRegisterRequest req);

    public ResponseResult<?> reviseAccountNickname(String jwt, ReviseAccountNicknameRequest req);

    public ResponseResult<?> resetAccountPassword(String jwt, ReviseAccountPasswordRequest req);

    public ResponseResult<?> retrieveBalanceWithCurrency(String jwt, RetrieveBalanceWithCurrencyRequest req);

    public ResponseResult<?> withdrawUnitedAccount(String jwt, WithdrawUnitedAccountRequest req);

    public ResponseResult<?> updateUnitedAccountStatus(String jwt, UpdateUnitedAccountStatusRequest req);

    public ResponseResult<?> updateSubAccountStatus(String jwt, UpdateSubAccountStatusRequest req);
}
