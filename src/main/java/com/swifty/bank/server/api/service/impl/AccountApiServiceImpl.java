package com.swifty.bank.server.api.service.impl;

import com.swifty.bank.server.api.controller.dto.account.request.AccountRegisterRequest;
import com.swifty.bank.server.api.controller.dto.account.request.RetrieveBalanceWithCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseAccountNicknameRequest;
import com.swifty.bank.server.api.controller.dto.account.request.ReviseUnitedAccountPasswordRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateDefaultCurrencyRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateSubAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.UpdateUnitedAccountStatusRequest;
import com.swifty.bank.server.api.controller.dto.account.request.WithdrawUnitedAccountRequest;
import com.swifty.bank.server.api.controller.dto.account.response.*;
import com.swifty.bank.server.api.service.AccountApiService;
import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.dto.*;
import com.swifty.bank.server.core.domain.account.service.AccountService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.core.domain.keypad.service.SecureKeypadService;
import com.swifty.bank.server.core.domain.keypad.service.dto.SecureKeypadDto;
import com.swifty.bank.server.core.domain.product.service.ProductService;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.core.utils.SBoxUtil;
import com.swifty.bank.server.exception.account.RequestorAndOwnerOfUnitedAccountIsDifferentException;
import java.util.HashMap;
import java.util.List;
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
    private final ProductService productService;

    private final SBoxKeyRedisService sBoxKeyRedisService;

    @Override
    public AccountRegisterResponse register(String accessToken, String keypadToken, AccountRegisterRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        Optional<Customer> customer = customerService.findByUuid(customerUuid);
        if (customer.isEmpty()) {
            return new AccountRegisterResponse(
                    false,
                    null
            );
        }

        // 비밀번호 복호화
        List<Integer> key = sBoxKeyRedisService.getData(keypadToken).getKey();
        List<Integer> decrypted = SBoxUtil.decrypt(req.getPushedOrder(), key);
        String password = String.join("",
                decrypted
                        .stream()
                        .map(Object::toString)
                        .toList()
        );

        AccountSaveDto dto = new AccountSaveDto(
                req.getProduct(),
                password,
                req.getCurrencies(),
                req.getDefaultCurrency(),
                customer.get()
        );

        UnitedAccount ua = accountService.saveUnitedAccountAndSubAccounts(dto);

        // redis에서 더 이상 필요 없는 임시 보관 데이터 삭제
        sBoxKeyRedisService.deleteData(keypadToken);

        return new AccountRegisterResponse(
                true,
                ua
        );
    }

    @Override
    public UpdateAccountNicknameResponse updateNickname(String accessToken, ReviseAccountNicknameRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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
    public ReviseUnitedAccountPasswordResponse updatePassword(String accessToken,
                                                              ReviseUnitedAccountPasswordRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        Optional<Customer> mayCustomer = customerService.findByUuid(customerUuid);
        if (mayCustomer.isEmpty()) {
            return new ReviseUnitedAccountPasswordResponse(
                    false
            );
        }

        // 비밀번호 복호화
        List<Integer> key = sBoxKeyRedisService.getData(accessToken).getKey();
        List<Integer> decrypted = SBoxUtil.decrypt(req.getPushedOrder(), key);
        String password = String.join("",
                decrypted
                        .stream()
                        .map(Object::toString)
                        .toList()
        );

        try {
            AccountPasswordUpdateDto dto = new AccountPasswordUpdateDto(
                    mayCustomer.get(), req.getAccountUuid(), password
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
    public RetrieveBalanceWithCurrencyResponse retrieveBalanceWithCurrency(String accessToken,
                                                                           RetrieveBalanceWithCurrencyRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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
    public WithdrawUnitedAccountResponse withdraw(String accessToken, WithdrawUnitedAccountRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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
    public UpdateUnitedAccountStatusResponse updateUnitedAccountStatus(String accessToken,
                                                                       UpdateUnitedAccountStatusRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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
    public UpdateSubAccountStatusResponse updateSubAccountStatus(String accessToken,
                                                                 UpdateSubAccountStatusRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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
    public UpdateDefaultCurrencyResponse updateDefaultCurrency(String accessToken, UpdateDefaultCurrencyRequest req) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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
    public ListUnitedAccountWithCustomerResponse listUnitedAccountWithCustomer(String accessToken) {
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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
                accountService.listUnitedAccountWithCustomer(dto).stream()
                        .map(sub -> new UnitedAccountDto(sub))
                        .toList()
        );
    }

    @Override
    public CreateSecureKeypadResponse createSecureKeypad() {
        SecureKeypadDto secureKeypadDto = secureKeypadService.createSecureKeypad();

        String keypadToken = secureKeypadService.createKeypadToken();
        // redis에 섞은 순서에 대한 정보 저장
        sBoxKeyRedisService.setData(
                keypadToken,
                SBoxKey.builder()
                        .key(secureKeypadDto.getKey())
                        .build()
        );

        return CreateSecureKeypadResponse.builder()
                .keypad(secureKeypadDto.getShuffledKeypadImages())
                .build();
    }

    @Override
    public ListOfAccountProductResponse accountProductList() {
        return ListOfAccountProductResponse.builder()
                .products(productService.findProductByProductType(ProductType.ACCOUNT))
                .build();
    }
}