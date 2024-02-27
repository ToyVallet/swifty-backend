package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.account.request.*;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AccountApiService {
    public ResponseResult<?> registerUnitedAccount(String token, AccountRegisterRequest req);

    public ResponseResult<?> reviseAccountNickname(String token, ReviseAccountNicknameRequest req);

    public ResponseResult<?> resetAccountPassword(String token, ReviseAccountPasswordRequest req);

    public ResponseResult<?> retrieveBalanceWithCurrency(String token, RetrieveBalanceWithCurrencyRequest req);

    public ResponseResult<?> withdrawUnitedAccount(String token, WithdrawUnitedAccountRequest req);
}
