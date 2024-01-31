package com.swifty.bank.server.api.service;

import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.dto.CustomerJoinDto;
import com.swifty.bank.server.core.domain.customer.dto.CustomerLoginWithDeviceIDDto;

public interface CustomerAPIService {
    ResponseResult<?> join(CustomerJoinDto dto);
    ResponseResult<?> login(CustomerLoginWithDeviceIDDto dto);
    ResponseResult<?> logout( );
}
