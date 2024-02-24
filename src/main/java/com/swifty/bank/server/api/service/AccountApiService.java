package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.account.dto.AccountRegisterRequest;
import com.swifty.bank.server.core.domain.account.dto.ReviseAccountNicknameRequest;

public interface AccountApiService {
    public ResponseResult<?> registerUnitedAccount(String token, AccountRegisterRequest req);

    public ResponseResult<?> reviseAccountNickname(String token, ReviseAccountNicknameRequest req);
}
