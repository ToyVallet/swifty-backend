package com.swifty.bank.server.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swifty.bank.server.api.service.AuthenticationApiService;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.customer.dto.JoinRequest;
import com.swifty.bank.server.core.common.authentication.dto.LoginWithFormRequest;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
        ObjectMapper mapper = new ObjectMapper( );
        try {
            Map<String, String> map = mapper.readValue(body, Map.class);
            String deviceId = map.get("deviceId");
            UUID uuid = jwtTokenUtil.getUuidFromToken(token);
            return authenticationApiService.loginWithJwt(uuid, deviceId);
        } catch (JsonMappingException e) {
            // pass to below
        } catch (JsonProcessingException e) {
            // pass to below
        }
        return new ResponseResult<>(
                Result.FAIL,
                "[ERROR] Json format is not valid",
                null
        );
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
        ResponseResult<?> res = authenticationApiService.join(body);
        return res;
    }
}