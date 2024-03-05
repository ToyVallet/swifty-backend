package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.*;
import com.swifty.bank.server.api.service.dto.ResponseResult;

public interface AuthenticationApiService {
    CheckLoginAvailabilityResponse checkLoginAvailability(
            CheckLoginAvailabilityRequest checkLoginAvailabilityRequest);

    SignWithFormResponse signUpAndSignIn(String temporaryToken, SignWithFormRequest dto);

    ReissueResponse reissue(String body);

    LogoutResponse logout(String token);

    SignoutResponse signOut(String token);
}
