package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.impl.PhoneAuthenticationServiceImpl;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.service.dto.CheckVerificationCodeRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendMessageRequest;
import com.swifty.bank.server.core.domain.sms.service.dto.SendVerificationCodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/sms")
@Slf4j
public class PhoneAuthenticationController {
    @Autowired
    private final PhoneAuthenticationServiceImpl phoneAuthenticationService;
    
    @PostMapping(value = "/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
        log.info("sendMessage() Started: " + sendMessageRequest.toString());
        ResponseResult<?> responseResult = phoneAuthenticationService.sendMessage(sendMessageRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PassAuth
    @PostMapping(value = "/sendVerificationCode")
    public ResponseEntity<?> sendVerificationCode(
            @RequestBody SendVerificationCodeRequest sendVerificationCodeRequest) {
        log.info("sendVerificationCodeRequest() Started: " + sendVerificationCodeRequest.toString());
        ResponseResult<?> responseResult = phoneAuthenticationService.sendVerificationCode(
                sendVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }

    @PassAuth
    @PostMapping(value = "/checkVerificationCode")
    public ResponseEntity<?> checkVerificationCode(
            @RequestBody CheckVerificationCodeRequest checkVerificationCodeRequest) {
        log.info("checkVerificationCode() Started: " + checkVerificationCodeRequest.toString());
        ResponseResult<?> responseResult = phoneAuthenticationService.checkVerificationCode(
                checkVerificationCodeRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}
