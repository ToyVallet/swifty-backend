package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.account.request.RetrieveBalanceWithCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountPasswordRequest;
import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.common.utils.JwtUtil;
import com.swifty.bank.server.core.domain.account.dto.AccountNicknameUpdateDto;
import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
import com.swifty.bank.server.core.domain.account.dto.AccountPasswordUpdateDto;
import com.swifty.bank.server.core.domain.account.dto.AccountSaveDto;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;
import com.swifty.bank.server.core.domain.account.dto.RetrieveBalanceOfUnitedAccountByCurrencyDto;
import com.swifty.bank.server.core.domain.account.exception.NoSuchUnitedAccountByUuidException;
import com.swifty.bank.server.core.domain.account.exception.RequestorAndOwnerOfUnitedAccountIsDifferentException;
import com.swifty.bank.server.core.domain.account.service.AccountService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountApiServiceImpl implements AccountApiService {
    private final AccountService accountService;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseResult<?> registerUnitedAccount(String token, AccountRegisterRequest req) {
        UUID uuid;

        try {
            uuid = UUID.fromString(JwtUtil.getClaimByKey(token, "customerId").toString());
        } catch (AuthenticationException e) {
            return new ResponseResult(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        Optional<Customer> customer = customerService.findByUuid(uuid);
        if (customer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 등록된 사용자가 없습니다. 오류가 발생한 UUID : ",
                    uuid
            );
        }

        AccountSaveDto dto = new AccountSaveDto(
                req.getBank(),
                req.getAccountPassword(),
                req.getCurrencies(),
                customer.get()
        );

        try {
            accountService.saveMultipleCurrencyAccount(dto);
        } catch (Exception e) { // 추후 구체적인 Customer Exception으로 교체 요망
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 성공적으로 계좌를 등록했습니다",
                null
        );
    }

    @Override
    public ResponseResult<?> reviseAccountNickname(String token, ReviseAccountNicknameRequest req) {
        UUID uuid;
        try {
            uuid = UUID.fromString(JwtUtil.getClaimByKey(token, "customerId").toString());
        } catch (AuthenticationException e) {
            return new ResponseResult(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        AccountNicknameUpdateDto dto = new AccountNicknameUpdateDto(
                uuid,
                req.getUnitedAccountUuid(),
                req.getNickname()
        );

        try {
            accountService.updateUaNickname(dto);
        } catch (NoSuchUnitedAccountByUuidException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] " + uuid.toString() + "의 계좌 닉네임 변경이 완료 되었습니다",
                null
        );
    }

    @Override
    public ResponseResult<?> resetAccountPassword(String token, ReviseAccountPasswordRequest req) {
        UUID uuid;
        try {
            uuid = UUID.fromString(JwtUtil.getClaimByKey(token, "customerId").toString());
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        try {
            AccountPasswordUpdateDto dto = new AccountPasswordUpdateDto(
                    uuid, req.getAccountUuid(), req.getPassword()
            );
            accountService.updateUaPassword(dto);
        } catch (NoSuchUnitedAccountByUuidException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 성공적으로 계좌 비밀번호 변경을 마쳤습니다.",
                null
        );
    }

    @Override
    public ResponseResult<?> retrieveBalanceWithCurrency(String token, RetrieveBalanceWithCurrencyRequest req) {
        UUID uuid;
        try {
            uuid = UUID.fromString(JwtUtil.getClaimByKey(token, "customerId").toString());
        } catch (AuthenticationException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        Map<String, Object> res = new HashMap<>();
        try {
            RetrieveBalanceOfUnitedAccountByCurrencyDto dto = new RetrieveBalanceOfUnitedAccountByCurrencyDto(
                    uuid, req.getUnitedAccountUuid(), req.getCurrency()
            );
            res.put("balance", accountService.retrieveBalanceByCurrency(dto));
        } catch (NoSuchUnitedAccountByUuidException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 계좌 조회가 성공적으로 완료되었습니다.",
                    res
                );
    }
}
