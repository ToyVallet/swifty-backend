package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.impl.PhoneAuthenticationServiceImpl;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.sms.service.dto.SendMessageRequest;
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
@RequestMapping(value = "/api")
@Slf4j
public class PhoneAuthenticationController {
    @Autowired
    private final PhoneAuthenticationServiceImpl phoneAuthenticationService;

    // sms 기능을 테스트하기 위한 임시 api입니다.
    @PostMapping(value = "/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
        log.info("sendMessage() Started sendMessageRequest: " + sendMessageRequest.toString());
        ResponseResult<?> responseResult = phoneAuthenticationService.sendMessage(sendMessageRequest);

        return ResponseEntity
                .ok()
                .body(responseResult);
    }
}
