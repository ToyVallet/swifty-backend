package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.authentication.dto.LoginWithFormRequest;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "auth")
public class AuthenticationController {
    private final AuthenticationApiService authenticationApiService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "sign-in-with-jwt")
    public ResponseResult<?> signInWithJwt(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody String body
    ) {
        return authenticationApiService.loginWithJwt(body, token);
    }

    @PassAuth
    @PostMapping("sign-in-with-form")
    public ResponseResult<?> signInWithForm(
            @RequestBody LoginWithFormRequest body
    ) {
        return authenticationApiService.loginWithForm(body.getDeviceId(), body.getPhoneNumber());
    }

    @PassAuth
    @PostMapping("sign-up-with-form")
    public ResponseResult<?> signUpWithForm(
            @RequestBody JoinRequest body
    ) {
        return authenticationApiService.join(body);
    }

    @PassAuth
    @PostMapping("/reissue")
    public ResponseResult<?> reissueTokens(
            @RequestBody String refToken
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.reissue(refToken)
                )
                .getBody();
    }

    @PostMapping("/log-out")
    public ResponseResult<?> logOut(
            @RequestHeader("Authorization") String token
    ) {
        return authenticationApiService.logout(token);
    }

    @PostMapping("/sign-out")
    public ResponseResult<?> signOut(
            @RequestHeader("Authorization") String token
    ) {
        return authenticationApiService.signOut(token);
    }
}