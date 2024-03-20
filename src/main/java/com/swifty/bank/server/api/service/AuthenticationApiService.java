package com.swifty.bank.server.api.service;

import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.StealVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.LogoutResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.ReissueResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SendVerificationCodeResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignOutResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignWithFormResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.StealVerificationCodeResponse;

public interface AuthenticationApiService {
    CheckLoginAvailabilityResponse checkLoginAvailability(
            CheckLoginAvailabilityRequest checkLoginAvailabilityRequest);

    SignWithFormResponse signUpAndSignIn(String temporaryToken, String keypadToken, SignWithFormRequest dto);

    StealVerificationCodeResponse stealVerificationCode(
            StealVerificationCodeRequest stealVerificationCodeRequest);

    SendVerificationCodeResponse sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest);

    CheckVerificationCodeResponse checkVerificationCode(
            CheckVerificationCodeRequest checkVerificationCodeRequest);

    CreateSecureKeypadResponse createSecureKeypad();

    ReissueResponse reissue(String body);

    LogoutResponse logout(String token);

    SignOutResponse signOut(String token);
}
