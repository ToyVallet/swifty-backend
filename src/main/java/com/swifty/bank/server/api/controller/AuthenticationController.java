package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.common.authentication.dto.LoginWithFormRequest;
import com.swifty.bank.server.core.domain.authentication.service.AuthenticationService;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationApiService authenticationApiService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "sign-in-with-jwt")
    public ResponseResult<?> signInWithJwt(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody String body
            ) {
        String deviceId = body.split(":")[1].trim( );
        UUID uuid = jwtTokenUtil.getUuidFromToken(token);
        return authenticationApiService.loginWithJwt(uuid, deviceId);
    }

    @PostMapping("sign-in-with-form")
    public ResponseResult<?> signInWithForm(
            @RequestBody LoginWithFormRequest body
    ) {
        return authenticationApiService.loginWithForm(body.getDeviceId(), body.getPhoneNumber());
    }

    @PostMapping("sign-up-with-form")
    public ResponseResult<?> signUpWithForm(
            @RequestBody JoinRequest body
    ) {
        return authenticationApiService.join(body);
    }
}