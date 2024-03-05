package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.ReissueRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.*;
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

    @TemporaryAuth
    @PostMapping("/sign-with-form")
    @Operation(summary = "회원가입과 로그인을 동시에 처리",
            description = "휴대폰 번호로 가입된 회원이 존재하면서 이름, 주민등록번호 정보가 불일치하는 경우만 실패")
    public ResponseEntity<SignWithFormResponse> signWithForm(
            @Parameter(description = "Temporary token with Authorization header", example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String temporaryToken,
            @RequestBody SignWithFormRequest body
    ) {
        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(
                JwtUtil.removePrefix(temporaryToken),
                body);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @PassAuth
    @PostMapping("/reissue")
    @Operation(summary = "reissue access and refresh token when access token got expired",
            description = "need valid refresh token. if refresh token is not valid too, try log in")
    public ResponseEntity<ReissueResponse> reissueTokens(
            @RequestBody ReissueRequest refToken
    ) {
        ReissueResponse res = authenticationApiService.reissue(JwtUtil.removePrefix(refToken.getRefreshToken()));

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/log-out")
    @Operation(summary = "log out with valid access token", description = "need valid access token")
    public ResponseEntity<?> logOut(
            @Parameter(description = "Access token with Authorization header", example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token
    ) {
        LogoutResponse res = authenticationApiService.logout(JwtUtil.extractJwtFromCurrentRequestHeader());

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/sign-out")
    @Operation(summary = "sign out with valid access token", description = "need valid access token")
    public ResponseEntity<?> signOut(
            @Parameter(description = "Access token with Authorization header", example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String token
    ) {
        SignoutResponse res = authenticationApiService.signOut(JwtUtil.extractJwtFromCurrentRequestHeader());

        return ResponseEntity
                .ok()
                .body(res);
    }
}