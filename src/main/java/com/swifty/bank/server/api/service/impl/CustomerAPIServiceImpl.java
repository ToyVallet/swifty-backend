package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.service.CustomerAPIService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.CustomerJoinDto;
import com.swifty.bank.server.core.domain.customer.dto.CustomerLoginWithDeviceIDDto;

public class CustomerAPIServiceImpl implements CustomerAPIService {
    @Override
    public ResponseResult<?> join(CustomerJoinDto dto) {
        return null;
    }

    @Override
    public ResponseResult<?> login(CustomerLoginWithDeviceIDDto dto) {
        return null;
    }

    @Override
    public ResponseResult<?> logout() {
        return null;
    }
}
