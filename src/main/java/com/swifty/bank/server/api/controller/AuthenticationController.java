package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.JoinRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.LoginWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.ReissueRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
@Tag(name = "Authentication API")
public class AuthenticationController {
    private final AuthenticationApiService authenticationApiService;

    @PassAuth
    @PostMapping("/check-login-availability")
    @Operation(summary = "회원가입/로그인 가능 여부 확인", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CheckLoginAvailabilityResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "요청 폼이 잘못된 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효한데 서버가 처리에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<CheckLoginAvailabilityResponse> checkLoginAvailability(
            @RequestBody CheckLoginAvailabilityRequest body
    ) {
        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(body);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @PassAuth
    @PostMapping("/sign-in-with-form")
    @Operation(summary = "sign in with form when user exist but doesn't have an access token to log in",
            description = "operate based on retrieval result of phone number and device id")
    public ResponseEntity<?> signInWithForm(
            @RequestBody LoginWithFormRequest body
    ) {
        ResponseResult res = authenticationApiService.loginWithForm(body.getDeviceId(), body.getPhoneNumber());
        if (res.getResult().equals(Result.FAIL)) {
            return ResponseEntity
                    .badRequest()
                    .body(res);
        }
        return ResponseEntity
                .ok()
                .body(res);
    }

    @PassAuth
    @PostMapping("/sign-up-with-form")
    @Operation(summary = "sign up with form which mean 회원가입 in Korean",
            description = "please follow adequate request form")
    public ResponseEntity<?> signUpWithForm(
            @RequestBody JoinRequest body
    ) {
        ResponseResult res = authenticationApiService.join(body);
        if (res.getResult().equals(Result.FAIL)) {
            return ResponseEntity
                    .badRequest()
                    .body(res);
        }
        return ResponseEntity
                .ok()
                .body(res);
    }

    @PassAuth
    @PostMapping("/reissue")
    @Operation(summary = "reissue access and refresh token when access token got expired",
            description = "need valid refresh token. if refresh token is not valid too, try log in")
    public ResponseEntity<?> reissueTokens(
            @RequestBody ReissueRequest refToken
    ) {
        ResponseResult res = authenticationApiService.reissue(refToken.getRefreshToken());
        if (res.getResult().equals(Result.FAIL)) {
            return ResponseEntity
                    .badRequest()
                    .body(res);
        }
        return ResponseEntity
                .ok()
                .body(res);
    }

    @PostMapping("/log-out")
    @Operation(summary = "log out with valid access token", description = "need valid access token")
    public ResponseEntity<?> logOut(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token
    ) {
        ResponseResult res = authenticationApiService.logout(JwtUtil.extractJwtFromCurrentRequestHeader());
        if (res.getResult().equals(Result.FAIL)) {
            return ResponseEntity
                    .badRequest()
                    .body(res);
        }
        return ResponseEntity
                .ok()
                .body(res);
    }

    @PostMapping("/sign-out")
    @Operation(summary = "sign out with valid access token", description = "need valid access token")
    public ResponseEntity<?> signOut(
            @Parameter(description = "Access token with Authorization header"
                    , example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token
    ) {
        ResponseResult res = authenticationApiService.signOut(JwtUtil.extractJwtFromCurrentRequestHeader());
        if (res.getResult().equals(Result.FAIL)) {
            return ResponseEntity
                    .badRequest()
                    .body(res);
        }
        return ResponseEntity
                .ok()
                .body(res);
    }
}