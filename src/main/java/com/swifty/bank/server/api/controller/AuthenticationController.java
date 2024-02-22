package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.authentication.dto.LoginWithFormRequest;
import com.swifty.bank.server.core.common.authentication.dto.ReissueRequest;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "auth")
@Tag(name = "Authentication API")
public class AuthenticationController {
    private final AuthenticationApiService authenticationApiService;

    @PassAuth
    @PostMapping("sign-in-with-form")
    @Operation(summary = "sign in with form when user exist but doesn't have an access token to log in",
            description = "operate based on retrieval result of phone number and device id")
    public ResponseEntity<?> signInWithForm(
            @RequestBody LoginWithFormRequest body
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.loginWithForm(body.getDeviceId(), body.getPhoneNumber()));
    }

    @PassAuth
    @PostMapping("sign-up-with-form")
    @Operation(summary = "sign up with form which mean 회원가입 in Korean",
            description = "please follow adequate request form")
    public ResponseEntity<?> signUpWithForm(
            @RequestBody JoinRequest body
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.join(body));
    }

    @PassAuth
    @PostMapping("/reissue")
    @Operation(summary = "reissue access and refresh token when access token got expired",
            description = "need valid refresh token. if refresh token is not valid too, try log in")
    public ResponseEntity<?> reissueTokens(
            @RequestBody ReissueRequest refToken
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.reissue(refToken.getRefreshToken())
                );
    }

    @PostMapping("/log-out")
    @Operation(summary = "log out with valid access token", description = "need valid access token")
    public ResponseEntity<?> logOut(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.logout(token));
    }

    @PostMapping("/sign-out")
    @Operation(summary = "sign out with valid access token", description = "need valid access token")
    public ResponseEntity<?> signOut(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity
                .ok()
                .body(authenticationApiService.signOut(token));
    }
}