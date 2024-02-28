package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.dto.sms.request.CheckVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.GetVerificationCodeRequest;
import com.swifty.bank.server.api.controller.dto.sms.request.SendVerificationCodeRequest;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.impl.PhoneAuthenticationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "인증번호 훔치기", description = "생성된 인증번호를 훔쳐봅니다.")
    @PostMapping(value = "/steal-verification-code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 훔치기에 성공했습니다."),
    })
    public ResponseEntity<?> stealVerificationCode(
            @RequestBody @Valid GetVerificationCodeRequest getVerificationCodeRequest) {
        ResponseResult<?> responseResult = phoneAuthenticationService.stealVerificationCode(
                getVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult.getData());
    }

    @PassAuth
    @Operation(summary = "인증번호 발송", description = "요청받은 전화번호로 인증번호를 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호가 정상적으로 발송되었습니다."),
            @ApiResponse(responseCode = "400", description = "인증번호 발송에 실패했습니다. (전화번호가 잘못된 경우, Twilio 서비스가 실패한 경우 등"),
    })
    @PostMapping(value = "/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(
            @RequestBody @Valid SendVerificationCodeRequest sendVerificationCodeRequest) {
        ResponseResult<?> responseResult = phoneAuthenticationService.sendVerificationCode(
                sendVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PassAuth
    @Operation(summary = "인증번호 확인", description = "인증번호가 올바른지 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호가 일치합니다."),
            @ApiResponse(responseCode = "400", description = "인증번호가 올바르지 않습니다."),
    })
    @PostMapping(value = "/check-verification-code")
    public ResponseEntity<?> checkVerificationCode(
            @RequestBody @Valid CheckVerificationCodeRequest checkVerificationCodeRequest) {
        ResponseResult<?> responseResult = phoneAuthenticationService.checkVerificationCode(
                checkVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}