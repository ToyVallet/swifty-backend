package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.JoinRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AuthenticationApiService {
    CheckLoginAvailabilityResponse checkLoginAvailability(
            CheckLoginAvailabilityRequest checkLoginAvailabilityRequest);

    ResponseResult<?> join(JoinRequest dto);

    ResponseResult<?> loginWithForm(String deviceId, String phoneNumber);

    ResponseResult<?> reissue(String body);

    ResponseResult<?> logout(String token);

    ResponseResult<?> signOut(String token);
}
