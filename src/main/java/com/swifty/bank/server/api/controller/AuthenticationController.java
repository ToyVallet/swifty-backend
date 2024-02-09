package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.authentication.dto.LoginWithFormRequest;
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
    public ResponseEntity<?> signInWithJwt(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody String body
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.loginWithJwt(body, token));
    }

    @PassAuth
    @PostMapping("sign-in-with-form")
    public ResponseEntity<?> signInWithForm(
            @RequestBody LoginWithFormRequest body
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.loginWithForm(body.getDeviceId(), body.getPhoneNumber()));
    }

    @PassAuth
    @PostMapping("sign-up-with-form")
    public ResponseEntity<?> signUpWithForm(
            @RequestBody JoinRequest body
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.join(body));
    }

    @PassAuth
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueTokens(
            @RequestBody String refToken
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.reissue(refToken)
                );
    }

    @PostMapping("/log-out")
    public ResponseEntity<?> logOut(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.logout(token));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.signOut(token));
    }
}