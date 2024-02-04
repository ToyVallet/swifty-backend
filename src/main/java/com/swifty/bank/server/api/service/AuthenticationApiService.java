package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;

import java.util.UUID;

public interface CustomerAPIService {
    ResponseResult<?> join(JoinRequest dto);
    ResponseResult<?> login(UUID uuid, String deviceId);
    ResponseResult<?> logout( );
}
