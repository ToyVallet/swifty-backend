package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.auth.request.JoinRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.VerifyCustomerExistenceRequest;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AuthenticationApiService {
    ResponseResult<?> verifyCustomerExistence(VerifyCustomerExistenceRequest verifyCustomerExistenceRequest);

    ResponseResult<?> join(JoinRequest dto);

    ResponseResult<?> loginWithForm(String deviceId, String phoneNumber);

    ResponseResult<?> reissue(String body);

    ResponseResult<?> logout(String token);

    ResponseResult<?> signOut(String token);
}
