package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountPasswordRequest;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;

public interface AccountApiService {
    public ResponseResult<?> registerUnitedAccount(String token, AccountRegisterRequest req);

    public ResponseResult<?> reviseAccountNickname(String token, ReviseAccountNicknameRequest req);

    public ResponseResult<?> resetAccountPassword(String toekn, ReviseAccountPasswordRequest req);
}
