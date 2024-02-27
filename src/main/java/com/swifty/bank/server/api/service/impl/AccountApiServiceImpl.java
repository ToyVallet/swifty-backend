package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.account.request.*;
import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.domain.account.dto.*;
import com.swifty.bank.server.exception.account.RequestorAndOwnerOfUnitedAccountIsDifferentException;
import com.swifty.bank.server.core.domain.account.service.AccountService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountApiServiceImpl implements AccountApiService {
    private final AccountService accountService;
    private final CustomerService customerService;

    @Override
    public ResponseResult<?> registerUnitedAccount(String token, AccountRegisterRequest req) {
        UUID uuid = JwtUtil.getValueByKeyWithObject(token, "customerId", UUID.class);

        Optional<Customer> customer = customerService.findByUuid(uuid);
        if (customer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 등록된 사용자가 없습니다. 오류가 발생한 UUID : ",
                    uuid
            );
        }

        AccountSaveDto dto = new AccountSaveDto(
                req.getProduct(),
                req.getAccountPassword(),
                req.getCurrencies(),
                req.getDefaultCurrency(),
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
        UUID uuid = JwtUtil.getValueByKeyWithObject(token, "customerId", UUID.class);

        Optional<Customer> customer = customerService.findByUuid(uuid);
        if (customer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 고객이 존재하지 않습니다.",
                    null
            );
        }

        AccountNicknameUpdateDto dto = new AccountNicknameUpdateDto(
                customer.get( ),
                req.getUnitedAccountUuid(),
                req.getNickname()
        );

        try {
            accountService.updateUaNickname(dto);
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
        UUID uuid = JwtUtil.getValueByKeyWithObject(token, "customerId", UUID.class);

        Optional<Customer> mayCustomer = customerService.findByUuid(uuid);
        if (mayCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 사용자가 없습니다.",
                    null
            );
        }

        try {
            AccountPasswordUpdateDto dto = new AccountPasswordUpdateDto(
                    mayCustomer.get(), req.getAccountUuid(), req.getPassword()
            );
            accountService.updateUaPassword(dto);
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
        UUID uuid = JwtUtil.getValueByKeyWithObject(token, "customerId", UUID.class);

        Optional<Customer> mayCustomer = customerService.findByUuid(uuid);
        if (mayCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 사용자가 없습니다.",
                    null
            );
        }

        Map<String, Object> res = new HashMap<>();
        try {
            RetrieveBalanceOfUnitedAccountByCurrencyDto dto = new RetrieveBalanceOfUnitedAccountByCurrencyDto(
                    mayCustomer.get(), req.getUnitedAccountUuid(), req.getCurrency()
            );
            res.put("balance", accountService.retrieveBalanceByCurrency(dto));
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

    @Override
    public ResponseResult<?> withdrawUnitedAccount(String token, WithdrawUnitedAccountRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerId", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 사용자가 존재하지 않습니다.",
                    null
            );
        }

        try {
            WithdrawUnitedAccountDto dto = new WithdrawUnitedAccountDto(
                    maybeCustomer.get(), req.getUnitedAccountId()
            );
            accountService.withdrawUnitedAccount(dto);
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 계좌 폐지가 성공적으로 마무리 되었습니다.",
                null
        );
    }
}
