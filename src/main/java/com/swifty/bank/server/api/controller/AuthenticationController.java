package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.api.controller.dto.MessageResponse;
import com.swifty.bank.server.api.controller.dto.auth.request.CheckLoginAvailabilityRequest;
import com.swifty.bank.server.api.controller.dto.auth.request.SignWithFormRequest;
import com.swifty.bank.server.api.controller.dto.auth.response.CheckLoginAvailabilityResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.LogoutResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.ReissueResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignOutResponse;
import com.swifty.bank.server.api.controller.dto.auth.response.SignWithFormResponse;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.utils.CookieUtils;
import com.swifty.bank.server.core.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
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
            @ApiResponse(responseCode = "400", description = "회원가입/로그인이 불가한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CheckLoginAvailabilityResponse.class))
                    })
    })
    public ResponseEntity<CheckLoginAvailabilityResponse> checkLoginAvailability(
            @Valid @RequestBody CheckLoginAvailabilityRequest body
    ) {
        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(body);
        if (res.getIsAvailable()) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE,
                            CookieUtils.createCookie("temporary-token", res.getTemporaryToken()).toString())
                    .body(res);
        }

        return ResponseEntity
                .badRequest()
                .body(res);
    }

    @TemporaryAuth
    @PostMapping("/sign-with-form")
    @Operation(summary = "신규 회원인 경우 회원가입과 로그인 순서대로 처리, 기존 회원의 경우 로그인 처리",
            description = "휴대폰 번호로 가입된 회원이 존재하면서 이름, 주민등록번호 정보가 불일치하는 경우 로그인 실패")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입/로그인에 성공한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SignWithFormResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "회원가입/로그인에 실패한 경우",
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
    public ResponseEntity<SignWithFormResponse> signWithForm(
            @CookieValue("temporary-token") String temporaryToken,
            @Valid @RequestBody SignWithFormRequest body
    ) {
        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(
                JwtUtil.removeType(temporaryToken),
                body);

        if (res.isSuccess()) {
            HttpHeaders headers = new HttpHeaders(
                    new LinkedMultiValueMap<>() {{
                        put(HttpHeaders.SET_COOKIE,
                                List.of(CookieUtils.createCookie("access-token", res.getTokens().get(0)).toString(),
                                        CookieUtils.createCookie("refresh-token", res.getTokens().get(1)).toString())
                        );
                    }}
            );
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(res);
        }

        return ResponseEntity
                .badRequest()
                .body(res);
    }

    @PassAuth
    @PostMapping("/reissue")
    @Operation(summary = "유효한 refresh token을 이용하여 access token, refresh token 재발급",
            description = "유효한 refresh token 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 발급된 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReissueResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "발급에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReissueResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효한데 서버가 처리에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<ReissueResponse> reissueTokens(
            @CookieValue("refresh-token") String refreshToken
    ) {
        ReissueResponse res = authenticationApiService.reissue(JwtUtil.removeType(refreshToken));

        if (res.getIsSuccess()) {
            HttpHeaders headers = new HttpHeaders(
                    new LinkedMultiValueMap<>() {{
                        put(HttpHeaders.SET_COOKIE,
                                List.of(CookieUtils.createCookie("access-token", res.getTokens().get(0)).toString(),
                                        CookieUtils.createCookie("refresh-token", res.getTokens().get(1)).toString())
                        );
                    }}
            );
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(res);
        }

        return ResponseEntity
                .badRequest()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/log-out")
    @Operation(summary = "로그아웃", description = "유효한 access token으로 요청해야 함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 로그아웃한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LogoutResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "로그아웃에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LogoutResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효한데 서버가 처리에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<LogoutResponse> logOut(
            @CookieValue("access-token") String accessToken
    ) {
        LogoutResponse res = authenticationApiService.logout(JwtUtil.removeType(accessToken));

        if (res.getIsSuccess()) {
            return ResponseEntity
                    .ok()
                    .body(res);
        }
        return ResponseEntity
                .badRequest()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/sign-out")
    @Operation(summary = "회원 탈퇴", description = "유효한 access token으로 요청해야 함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 회원 탈퇴한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SignOutResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "회원 탈퇴에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SignOutResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "클라이언트의 요청은 유효한데 서버가 처리에 실패한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class))
                    })
    })
    public ResponseEntity<SignOutResponse> signOut(
            @CookieValue("access-token") String accessToken
    ) {
        SignOutResponse res = authenticationApiService.signOut(JwtUtil.removeType(accessToken));

        if (res.getIsSuccess()) {
            return ResponseEntity
                    .ok()
                    .body(res);
        }
        return ResponseEntity
                .badRequest()
                .body(res);
    }
}