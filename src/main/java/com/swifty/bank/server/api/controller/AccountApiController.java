package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.core.common.response.ResponseResult;
import com.swifty.bank.server.core.domain.account.dto.AccountRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/account")
public class AccountApiController {
    private final AccountApiService accountApiService;

    public ResponseEntity<?> register(@RequestHeader("authorization") String token, @RequestBody AccountRegisterRequest req) {
        ResponseResult<?> res = accountApiService.registerUnitedAccount(token, req);

        return ResponseEntity
                .ok()
                .body(res);
    }
}
