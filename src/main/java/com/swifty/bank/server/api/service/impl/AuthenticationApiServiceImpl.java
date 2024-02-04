package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;

import java.util.UUID;

public class CustomerAPIServiceImpl implements CustomerAPIService {
    @Override
    public ResponseResult<?> join(JoinRequest dto) {
        return null;
    }

    @Override
    public ResponseResult<?> login(UUID uuid, String deviceId) {
        return null;
    }

    @Override
    public ResponseResult<?> logout() {
        return null;
    }
}
