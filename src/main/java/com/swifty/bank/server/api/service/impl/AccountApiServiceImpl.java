package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
import com.swifty.bank.server.api.controller.dto.account.request.RetrieveBalanceWithCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseUnitedAccountPasswordRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateDefaultCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateSubAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateUnitedAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.WithdrawUnitedAccountRequest;
import com.swifty.bank.server.api.controller.dto.account.response.AccountRegisterResponse;
import com.swifty.bank.server.api.controller.dto.account.response.CreateSecureKeypadResponse;
import com.swifty.bank.server.api.controller.dto.account.response.ListUnitedAccountWithCustomerResponse;
import com.swifty.bank.server.api.controller.dto.account.response.RetrieveBalanceWithCurrencyResponse;
import com.swifty.bank.server.api.controller.dto.account.response.ReviseUnitedAccountPasswordResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateAccountNicknameResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateDefaultCurrencyResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateSubAccountStatusResponse;
import com.swifty.bank.server.api.controller.dto.account.response.UpdateUnitedAccountStatusResponse;
import com.swifty.bank.server.api.controller.dto.account.response.WithdrawUnitedAccountResponse;
import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.value.SBoxKey;
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
import com.swifty.bank.server.core.domain.keypad.service.SecureKeypadService;
import com.swifty.bank.server.core.domain.keypad.service.dto.SecureKeypadDto;
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
    private final SecureKeypadService secureKeypadService;

    private final SBoxKeyRedisService sBoxKeyRedisService;

    @Override
    public AccountRegisterResponse register(String token, AccountRegisterRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> customer = customerService.findByUuid(customerUuid);
        if (customer.isEmpty()) {
            return new AccountRegisterResponse(
                    false
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

        return new AccountRegisterResponse(
                true
        );
    }

    @Override
    public UpdateAccountNicknameResponse updateNickname(String token, ReviseAccountNicknameRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> customer = customerService.findByUuid(customerUuid);
        if (customer.isEmpty()) {
            return new UpdateAccountNicknameResponse(
                    false
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
            return new UpdateAccountNicknameResponse(
                    false
            );
        }

        return new UpdateAccountNicknameResponse(
                true
        );
    }

    @Override
    public ReviseUnitedAccountPasswordResponse updatePassword(String token, ReviseUnitedAccountPasswordRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> mayCustomer = customerService.findByUuid(customerUuid);
        if (mayCustomer.isEmpty()) {
            return new ReviseUnitedAccountPasswordResponse(
                    false
            );
        }

        try {
            AccountPasswordUpdateDto dto = new AccountPasswordUpdateDto(
                    mayCustomer.get(), req.getAccountUuid(), req.getPassword()
            );
            accountService.updateUnitedAccountPassword(dto);
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new ReviseUnitedAccountPasswordResponse(
                    false
            );
        }

        return new ReviseUnitedAccountPasswordResponse(
                true
        );
    }

    @Override
    public RetrieveBalanceWithCurrencyResponse retrieveBalanceWithCurrency(String token,
                                                                           RetrieveBalanceWithCurrencyRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> mayCustomer = customerService.findByUuid(customerUuid);
        if (mayCustomer.isEmpty()) {
            return new RetrieveBalanceWithCurrencyResponse(
                    false,
                    null
            );
        }

        Map<String, Double> res = new HashMap<>();
        try {
            RetrieveBalanceOfUnitedAccountByCurrencyDto dto = new RetrieveBalanceOfUnitedAccountByCurrencyDto(
                    mayCustomer.get(), req.getUnitedAccountUuid(), req.getCurrency()
            );
            res.put(req.getCurrency().toString(), accountService.retrieveBalanceByCurrency(dto));
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new RetrieveBalanceWithCurrencyResponse(
                    false,
                    null
            );
        }

        return new RetrieveBalanceWithCurrencyResponse(
                true,
                res
        );
    }

    @Override
    public WithdrawUnitedAccountResponse withdraw(String token, WithdrawUnitedAccountRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(token, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new WithdrawUnitedAccountResponse(
                    false
            );
        }

        try {
            WithdrawUnitedAccountDto dto = new WithdrawUnitedAccountDto(
                    maybeCustomer.get(), req.getUnitedAccountId()
            );
            accountService.withdrawUnitedAccount(dto);
        } catch (RequestorAndOwnerOfUnitedAccountIsDifferentException e) {
            return new WithdrawUnitedAccountResponse(
                    false
            );
        }

        return new WithdrawUnitedAccountResponse(
                true
        );
    }

    @Override
    public UpdateUnitedAccountStatusResponse updateUnitedAccountStatus(String jwt,
                                                                       UpdateUnitedAccountStatusRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new UpdateUnitedAccountStatusResponse(
                    false
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
            return new UpdateUnitedAccountStatusResponse(
                    false
            );
        }

        return new UpdateUnitedAccountStatusResponse(
                true
        );
    }

    @Override
    public UpdateSubAccountStatusResponse updateSubAccountStatus(String jwt, UpdateSubAccountStatusRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new UpdateSubAccountStatusResponse(
                    false
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
            return new UpdateSubAccountStatusResponse(
                    false
            );
        }

        return new UpdateSubAccountStatusResponse(
                true
        );
    }

    @Override
    public UpdateDefaultCurrencyResponse updateDefaultCurrency(String jwt, UpdateDefaultCurrencyRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new UpdateDefaultCurrencyResponse(
                    false
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
            return new UpdateDefaultCurrencyResponse(
                    false
            );
        }

        return new UpdateDefaultCurrencyResponse(
                true
        );
    }

    @Override
    public ListUnitedAccountWithCustomerResponse listUnitedAccountWithCustomer(String jwt) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(jwt, "customerUuid", UUID.class);

        Optional<Customer> maybeCustomer = customerService.findByUuid(customerUuid);
        if (maybeCustomer.isEmpty()) {
            return new ListUnitedAccountWithCustomerResponse(
                    false,
                    null
            );
        }

        ListUnitedAccountWithCustomerDto dto = new ListUnitedAccountWithCustomerDto(maybeCustomer.get());

        return new ListUnitedAccountWithCustomerResponse(
                true,
                accountService.listUnitedAccountWithCustomer(dto)
        );
    }

    @Override
    public CreateSecureKeypadResponse createSecureKeypad(String accessToken) {
        SecureKeypadDto secureKeypadDto = secureKeypadService.createSecureKeypad();

        // redis에 섞은 순서에 대한 정보 저장
        sBoxKeyRedisService.setData(
                accessToken,
                SBoxKey.builder()
                        .key(secureKeypadDto.getKey())
                        .build()
        );

        return CreateSecureKeypadResponse.builder()
                .keypad(secureKeypadDto.getShuffledKeypadImages())
                .build();
    }
}