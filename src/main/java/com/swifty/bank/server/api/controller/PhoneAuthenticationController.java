package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.impl.PhoneAuthenticationServiceImpl;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.service.dto.CheckVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.GetVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendVerificationCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/sms")
@Slf4j
@Tag(name = "API for phone authentication")
public class PhoneAuthenticationController {
    private final PhoneAuthenticationServiceImpl phoneAuthenticationService;

    @PassAuth
    @Operation(summary = "send verification code in response body")
    @PostMapping(value = "/get-verification-code")
    public ResponseEntity<?> getVerificationCode(
            @RequestBody @Valid GetVerificationCodeRequest getVerificationCodeRequest) {
        log.info("getVerificationCode() Started: " + getVerificationCodeRequest.toString());
        ResponseResult<?> responseResult = phoneAuthenticationService.getVerificationCode(
                getVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PassAuth
    @Operation(summary = "send a verification code message")
    @PostMapping(value = "/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(
            @RequestBody @Valid SendVerificationCodeRequest sendVerificationCodeRequest) {
        log.info("sendVerificationCodeRequest() Started: " + sendVerificationCodeRequest.toString());
        ResponseResult<?> responseResult = phoneAuthenticationService.sendVerificationCode(
                sendVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PassAuth
    @Operation(summary = "check whether verification code is equal or not")
    @PostMapping(value = "/check-verification-code")
    public ResponseEntity<?> checkVerificationCode(
            @RequestBody @Valid CheckVerificationCodeRequest checkVerificationCodeRequest) {
        log.info("checkVerificationCode() Started: " + checkVerificationCodeRequest.toString());
        ResponseResult<?> responseResult = phoneAuthenticationService.checkVerificationCode(
                checkVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}