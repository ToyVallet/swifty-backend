package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AuthenticationApiService {
    CheckLoginAvailabilityResponse checkLoginAvailability(
            CheckLoginAvailabilityRequest checkLoginAvailabilityRequest);

    ResponseResult<?> enrollOrSignIn(String jwt, SignRequest dto);

    ResponseResult<?> reissue(String body);

    ResponseResult<?> logout(String token);

    ResponseResult<?> signOut(String token);
}
