package com.swifty.bank.server.api.controller;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
import com.swifty.bank.server.api.controller.dto.account.request.RetrieveBalanceWithCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseUnitedAccountPasswordRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateDefaultCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateSubAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateUnitedAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.WithdrawUnitedAccountRequest;
import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/account")
public class AccountApiController {
    private final AccountApiService accountApiService;

    @CustomerAuth
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestHeader("Authorization") String jwt,
                                      @RequestBody AccountRegisterRequest req) {
        ResponseResult<?> res = accountApiService.register(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PatchMapping(value = "/update_nickname")
    public ResponseEntity<?> updateNickname(
            @RequestHeader("Authorization") String jwt,
            @RequestBody ReviseAccountNicknameRequest req
    ) {
        ResponseResult<?> res = accountApiService.updateNickname(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PatchMapping(value = "/update_password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            ReviseUnitedAccountPasswordRequest req
    ) {
        ResponseResult<?> res = accountApiService.updatePassword(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @GetMapping("/retrieve_balance")
    public ResponseEntity<?> retrieveBalanceWithCurrency(
            @RequestHeader("authorization")
            String jwt,
            @RequestBody
            RetrieveBalanceWithCurrencyRequest req
    ) {
        ResponseResult<?> res = accountApiService.retrieveBalanceWithCurrency(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawAccount(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            WithdrawUnitedAccountRequest req
    ) {
        ResponseResult<?> res = accountApiService.withdraw(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/update_status")
    public ResponseEntity<?> updateUnitedAccount(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            UpdateUnitedAccountStatusRequest req
    ) {
        ResponseResult<?> res = accountApiService.updateUnitedAccountStatus(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping("/update_currency_status")
    public ResponseEntity<?> updateSubAccountStatus(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            UpdateSubAccountStatusRequest req
    ) {
        ResponseResult<?> res = accountApiService.updateSubAccountStatus(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @PostMapping(value = "/update_default_currency")
    public ResponseEntity<?> updateSubAccountStatus(
            @RequestHeader("Authorization")
            String jwt,
            @RequestBody
            UpdateDefaultCurrencyRequest req
    ) {
        ResponseResult<?> res = accountApiService.updateDefaultCurrency(jwt, req);

        return ResponseEntity
                .ok()
                .body(res);
    }

    @CustomerAuth
    @GetMapping(value = "/list")
    public ResponseEntity<?> listUnitedAccountWithCustomer(
            @RequestHeader("Authorization")
            String jwt
    ) {
        ResponseResult<?> res = accountApiService.listUnitedAccountWithCustomer(jwt);

        return ResponseEntity
                .ok()
                .body(res);
    }
}
