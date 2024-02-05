package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;

import java.util.UUID;

public interface AuthenticationApiService {
    ResponseResult<?> join(JoinRequest dto);

    ResponseResult<?> loginWithJwt(UUID uuid, String deviceId);

    ResponseResult<?> loginWithForm(String deviceId, String phoneNumber);

    ResponseResult<?> reissue(UUID uuid);

    ResponseResult<?> logout(UUID uuid);
}
