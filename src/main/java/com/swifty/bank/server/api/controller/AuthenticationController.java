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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody CheckLoginAvailabilityRequest body,
            HttpServletResponse response
    ) {
        CheckLoginAvailabilityResponse res = authenticationApiService.checkLoginAvailability(body);
        ResponseCookie temporaryCookie =
                CookieUtils.createCookie("temporaryToken", res.getTemporaryToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, temporaryCookie.toString())
                .body(res);
    }

    @TemporaryAuth
    @PostMapping("/sign-with-form")
    @Operation(summary = "신규 회원인 경우 회원가입과 로그인 순서대로 처리, 기존 회원의 경우 로그인 처리",
            description = "휴대폰 번호로 가입된 회원이 존재하면서 이름, 주민등록번호 정보가 불일치하는 경우 로그인 실패")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SignWithFormResponse.class))
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
    public ResponseEntity<SignWithFormResponse> signWithForm(
            @CookieValue("temporaryToken") String temporaryToken,
            @Valid @RequestBody SignWithFormRequest body
    ) {
        SignWithFormResponse res = authenticationApiService.signUpAndSignIn(
                JwtUtil.removeType(temporaryToken),
                body);

        ResponseCookie accessCookie =
                CookieUtils.createCookie("accessToken", res.getTokens().get(0));
        ResponseCookie refreshCookie =
                CookieUtils.createCookie("refreshToken", res.getTokens().get(1));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }

    @PassAuth
    @PostMapping("/reissue")
    @Operation(summary = "액세스 토큰 만료시 리프레시 토큰을 이용한 토큰들 재발급",
            description = "유효한 리프레시 토큰 필요, 만약 리프레시 토큰도 만료시 재로그인 필요함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReissueResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "헤더의 리프레시 토큰이 잘못된 경우",
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
    public ResponseEntity<ReissueResponse> reissueTokens(
            @CookieValue("refreshToken") String refreshToken
    ) {
        ReissueResponse res = authenticationApiService.reissue(JwtUtil.removeType(refreshToken));

        ResponseCookie accessCookie =
                CookieUtils.createCookie("accessToken", res.getTokens().get(0));
        ResponseCookie refreshCookie =
                CookieUtils.createCookie("refreshToken", res.getTokens().get(1));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/log-out")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LogoutResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "헤더의 토큰이 잘못된 경우",
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
    @Operation(summary = "유효한 유저가 로그인 하게 함", description = "이를 시도하는 유저는 로그인 되어 있는 상태여야 하며 액세스 토큰 역시 유효해야 함")
    public ResponseEntity<LogoutResponse> logOut(
            @Parameter(description = "Authorization에 AccessToken을 포함시켜 주세요", example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String accessToken
    ) {
        LogoutResponse res = authenticationApiService.logout(JwtUtil.removeType(accessToken));

        ResponseCookie accessCookie =
                CookieUtils.createCookie("accessToken", "");
        ResponseCookie refreshCookie =
                CookieUtils.createCookie("refreshToken", "");

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/sign-out")
    @Operation(summary = "유효한 유저의 회원 탈퇴 기능", description = "유효한 액세스 토큰이 필요하며 유저는 로그인 되어 있어야 함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 확인한 경우",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SignOutResponse.class))
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
    public ResponseEntity<SignOutResponse> signOut(
            @Parameter(description = "Authorization에 AccessToken을 포함시켜 주세요", example = "Bearer ey...", required = true)
            @RequestHeader("Authorization") String accessToken
    ) {
        SignOutResponse res = authenticationApiService.signOut(JwtUtil.removeType(accessToken));

        ResponseCookie accessCookie =
                CookieUtils.createCookie("accessToken", "");
        ResponseCookie refreshCookie =
                CookieUtils.createCookie("refreshToken", "");

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }
}