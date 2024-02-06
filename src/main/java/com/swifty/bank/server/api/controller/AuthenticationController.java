package com.swifty.bank.server.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.authentication.dto.LoginWithFormRequest;
import com.swifty.bank.server.core.common.authentication.exception.AuthenticationException;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

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
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> map = mapper.readValue(body, Map.class);
            String deviceId = map.get("deviceId");
            UUID uuid = jwtTokenUtil.getUuidFromToken(token);
            return authenticationApiService.loginWithJwt(uuid, deviceId);
        } catch (JsonProcessingException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Json format is not valid",
                    null
            );
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Authentication is not valid",
                    null
            );
        }
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
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> map = mapper.readValue(refToken, Map.class);
            String token = map.get("RefreshToken");

            UUID uuid = jwtTokenUtil.getUuidFromToken(token);
            return authenticationApiService.reissue(uuid, token);
        } catch (JsonProcessingException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Json format is not valid",
                    null
            );
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] Authentication is not valid",
                    null
            );
        }
    }

    @PostMapping("/log-out")
    public ResponseResult<?> logOut(
            @RequestHeader("Authorization") String token
    ) {
        try {
            UUID uuid = jwtTokenUtil.getUuidFromToken(token);

            return authenticationApiService.logout(uuid);
        } catch (AuthenticationException e) {
            return new ResponseResult(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }
    }
}