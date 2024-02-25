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
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/account")
public class AccountApiController {
    private final AccountApiService accountApiService;

    @PostMapping(value = "/register_account")
    public ResponseEntity<?> register(@RequestHeader("authorization") String token, @RequestBody AccountRegisterRequest req) {
        ResponseResult<?> res = accountApiService.registerUnitedAccount(token, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @PatchMapping(value = "/update_account_nickname")
    public ResponseEntity<?> updateNickname(
            @RequestHeader("authorization") String token,
            @RequestBody ReviseAccountNicknameRequest req
    ) {
        ResponseResult<?> res = accountApiService.reviseAccountNickname(token, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @PatchMapping(value = "/reset_account_password")
    public ResponseEntity<?> resetAccountPassword(
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

    @GetMapping("/retrieve_balance")
    public ResponseEntity<?> retrieveBalanceWithCurrency(
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
