package com.swifty.bank.server.api.service.impl;

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
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.domain.account.dto.AccountNicknameUpdateDto;
import com.swifty.bank.server.core.domain.account.dto.AccountPasswordUpdateDto;
import com.swifty.bank.server.core.domain.account.dto.AccountSaveDto;
import com.swifty.bank.server.core.domain.account.dto.ListUnitedAccountWithCustomerDto;
import com.swifty.bank.server.core.domain.account.dto.RetrieveBalanceOfUnitedAccountByCurrencyDto;
import com.swifty.bank.server.core.domain.account.dto.UpdateDefaultCurrencyDto;
import com.swifty.bank.server.core.domain.account.dto.UpdateSubAccountStatusDto;
import com.swifty.bank.server.core.domain.account.dto.UpdateUnitedAccountStatusDto;
import com.swifty.bank.server.core.domain.account.dto.WithdrawUnitedAccountDto;
import com.swifty.bank.server.core.domain.account.service.AccountService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.exception.account.RequestorAndOwnerOfUnitedAccountIsDifferentException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountApiServiceImpl implements AccountApiService {
    private final AccountService accountService;
    private final CustomerService customerService;

    @Override
    public ResponseResult<?> register(String token, AccountRegisterRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> customer = customerService.findByUuid(customerUuid);
        if (customer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 등록된 사용자가 없습니다. 오류가 발생한 UUID : ",
                    customerUuid
            );
        }

        AccountSaveDto dto = new AccountSaveDto(
                req.getProduct(),
                req.getAccountPassword(),
                req.getCurrencies(),
                req.getDefaultCurrency(),
                customer.get()
        );

        accountService.saveUnitedAccountAndSubAccounts(dto);

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 성공적으로 계좌를 등록했습니다",
                null
        );
    }

    @Override
    public ResponseResult<?> updateNickname(String token, ReviseAccountNicknameRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> customer = customerService.findByUuid(customerUuid);
        if (customer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 고객이 존재하지 않습니다.",
                    null
            );
        }

        AccountNicknameUpdateDto dto = new AccountNicknameUpdateDto(
                customer.get(),
                req.getUnitedAccountUuid(),
                req.getNickname()
        );

        try {
            accountService.updateUnitedAccountNickname(dto);
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] " + customerUuid.toString() + "의 계좌 닉네임 변경이 완료 되었습니다",
                null
        );
    }

    @Override
    public ResponseResult<?> updatePassword(String token, ReviseUnitedAccountPasswordRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> mayCustomer = customerService.findByUuid(customerUuid);
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
            accountService.updateUnitedAccountPassword(dto);
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
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> mayCustomer = customerService.findByUuid(customerUuid);
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
    public ResponseResult<?> withdraw(String token, WithdrawUnitedAccountRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

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

    @Override
    public ResponseResult<?> updateUnitedAccountStatus(String jwt, UpdateUnitedAccountStatusRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 사용자가 없습니다.",
                    null
            );
        }

        try {
            UpdateUnitedAccountStatusDto dto = new UpdateUnitedAccountStatusDto(
                    maybeCustomer.get(),
                    req.getUnitedAccountUuid(),
                    req.getStatus()
            );
            accountService.updateUnitedAccountStatus(dto);
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 통합 계좌 상태 업데이트가 성공적으로 마무리 되었습니다.",
                null
        );
    }

    @Override
    public ResponseResult<?> updateSubAccountStatus(String jwt, UpdateSubAccountStatusRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 사용자가 없습니다.",
                    null
            );
        }

        try {
            UpdateSubAccountStatusDto dto = new UpdateSubAccountStatusDto(
                    maybeCustomer.get(),
                    req.getUnitedAccountUuid(),
                    req.getCurrency(),
                    req.getAccountStatus()
            );
            accountService.updateSubAccountStatus(dto);
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 통합 계좌의 환계좌 상태 업데이트가 성공적으로 마무리 되었습니다.",
                null
        );
    }

    @Override
    public ResponseResult<?> updateDefaultCurrency(String jwt, UpdateDefaultCurrencyRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 사용자가 없습니다.",
                    null
            );
        }

        try {
            UpdateDefaultCurrencyDto dto = new UpdateDefaultCurrencyDto(
                    maybeCustomer.get(),
                    req.getUnitedAccountUuid(),
                    req.getDefaultCurrency()
            );
            accountService.updateDefaultCurrency(dto);
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ResponseResult<>(
                    Result.FAIL,
                    e.getMessage(),
                    null
            );
        }

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 통합 계좌의 기본 환이 변경되었습니다.",
                null
        );
    }

    @Override
    public ResponseResult<?> listUnitedAccountWithCustomer(String jwt) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new ResponseResult<>(
                    Result.FAIL,
                    "[ERROR] 해당 사용자가 없습니다.",
                    null
            );
        }

        ListUnitedAccountWithCustomerDto dto = new ListUnitedAccountWithCustomerDto(maybeCustomer.get());

        return new ResponseResult<>(
                Result.SUCCESS,
                "[INFO] 특정 고객이 가진 통합 계좌들입니다.",
                accountService.listUnitedAccountWithCustomer(dto)
        );
    }
}
