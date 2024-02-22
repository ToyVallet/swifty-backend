package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;

public interface AuthenticationApiService {
    ResponseResult<?> join(JoinRequest dto);

    ResponseResult<?> loginWithForm(String deviceId, String phoneNumber);

    ResponseResult<?> reissue(String body);

    ResponseResult<?> logout(String token);

    ResponseResult<?> signOut(String token);
}
