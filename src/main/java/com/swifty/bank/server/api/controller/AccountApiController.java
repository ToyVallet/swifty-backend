package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.dto.account.request.RetrieveBalanceWithCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountPasswordRequest;
import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
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

    public ResponseEntity<?> updateNickname(
            @RequestHeader("authorization") String token,
            @RequestBody ReviseAccountNicknameRequest req
    ) {
        ResponseResult<?> res = accountApiService.reviseAccountNickname(token, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    public ResponseEntity<?> register(
            @RequestHeader("authorization")
            String token,
            @RequestBody
            ReviseAccountPasswordRequest req
    ) {
        ResponseResult<?> res = accountApiService.resetAccountPassword(token, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    public ResponseEntity<?> register(
            @RequestHeader("authorization")
            String token,
            @RequestBody
            RetrieveBalanceWithCurrencyRequest req
    ) {
        ResponseResult<?> res = accountApiService.retrieveBalanceWithCurrency(token, req);

        return ResponseEntity
                .ok()
                .body(res);
    }
}
