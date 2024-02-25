package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.GetVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.api.service.impl.PhoneAuthenticationServiceImpl;
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
    @PostMapping(value = "/steal-verification-code")
    public ResponseEntity<?> stealVerificationCode(
            @RequestBody @Valid GetVerificationCodeRequest getVerificationCodeRequest) {
        ResponseResult<?> res = phoneAuthenticationService.stealVerificationCode(
                getVerificationCodeRequest);

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
    @Operation(summary = "send a verification code message")
    @PostMapping(value = "/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(
            @RequestBody @Valid SendVerificationCodeRequest sendVerificationCodeRequest) {
        ResponseResult<?> res = phoneAuthenticationService.sendVerificationCode(
                sendVerificationCodeRequest);

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
    @Operation(summary = "check whether verification code is equal or not")
    @PostMapping(value = "/check-verification-code")
    public ResponseEntity<?> checkVerificationCode(
            @RequestBody @Valid CheckVerificationCodeRequest checkVerificationCodeRequest) {
        ResponseResult<?> res = phoneAuthenticationService.checkVerificationCode(
                checkVerificationCodeRequest);

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