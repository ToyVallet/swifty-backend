package com.swifty.bank.server.core.domain.account.service.impl;

import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.SubAccount;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.dto.*;
import com.swifty.bank.server.exception.account.RequestorAndOwnerOfUnitedAccountIsDifferentException;
import com.swifty.bank.server.core.domain.account.repository.SubAccountRepository;
import com.swifty.bank.server.core.domain.account.repository.UnitedAccountRepository;
import com.swifty.bank.server.core.domain.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UnitedAccountRepository unitedAccountRepository;
    private final SubAccountRepository subAccountRepository;
    @Override
    public UnitedAccount saveMultipleCurrencyAccount(AccountSaveDto dto) {
        UnitedAccount ua = UnitedAccount.builder()
                .accountPassword(dto.getAccountPassword())
                .accountNumber(generateAccountNumberWithModulus10())
                .product(dto.getProduct())
                .defaultCurrency(dto.getDefaultCurrency())
                .customer(dto.getCustomer())

                .build();

        dto.getCustomer().addUnitedAccount(ua);

        UnitedAccount createdAccount = unitedAccountRepository.save(ua);

        for (Currency cur : dto.getCurrencies()) {
            SubAccount sa = SubAccount.builder()
                    .ua(createdAccount)
                    .cur(cur)
                    .build();

            subAccountRepository.save(sa);
            ua.addSubAccount(sa);
        }

        return createdAccount;
    }

    @Override
    public void updateUaNickname(AccountNicknameUpdateDto dto) {
        Optional<UnitedAccount> ua = dto.getCustomer( ).findUnitedAccountByUnitedAccountId(dto.getUaUuid( ));

        if (ua.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 UUID로 등록된 통합 계좌가 없습니다.");
        }

        UUID accountOwnerUuid = ua.get().getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer( ).getId( )) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 수정 요청자와 소유자가 다릅니다.");
        }

        ua.get().updateNickname(dto.getNickname());
    }

    @Override
    public void updateUaPassword(AccountPasswordUpdateDto dto) {
        Optional<UnitedAccount> ua = dto.getCustomer( ).findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid( ));

        if (ua.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 UUID로 등록된 통합 계좌가 없습니다.");
        }

        UUID accountOwnerUuid = ua.get().getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 수정 요청자와 소유자가 다릅니다.");
        }

        ua.get().updatePassword(dto.getPassword());
    }

    @Override
    public double retrieveBalanceByCurrency(RetrieveBalanceOfUnitedAccountByCurrencyDto dto) {
        Optional<UnitedAccount> ua = dto.getCustomer( ).findUnitedAccountByUnitedAccountId(dto.getUntiedAccountId( ));

        if (ua.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 UUID로 등록된 통합 계좌가 없습니다");
        }

        UUID accountOwnerUuid = ua.get().getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 조회 요청자와 소유자가 다릅니다");
        }

        Optional<SubAccount> sa = ua.get().findSubAccountByCurrency(dto.getCurrency( ));

        if (sa.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 계좌 아이디로 등록된 환 계좌가 없습니다.");
        }

        return sa.get().getBalance();
    }

    @Override
    public void withdrawUnitedAccount(WithdrawUnitedAccountDto dto) {
        Optional<UnitedAccount> ua = dto.getCustomer().findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid( ));

        if (ua.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 UUID로 등록된 통합 계좌가 없습니다.");
        }

        UUID accountOwnerUuid = ua.get().getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 조회 요청자와 소유자가 다릅니다.");
        }

        ua.get().delete();
    }

    @Override
    public void updateUnitedAccountStatus(UpdateUnitedAccountStatusDto dto) {
        Optional<UnitedAccount> ua = dto.getCustomer().findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid());

        if (ua.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 UUID로 등록된 통합 계좌가 없습니다.");
        }

        UUID accountOwnerUuid = ua.get().getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) == 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROr] 계좌의 조회 요청자와 소유자가 다릅니다.");
        }

        ua.get().updateStatus(dto.getAccountStatus());
    }

    @Override
    public void updateSubAccountStatus(UpdateSubAccountStatusDto dto) {
        Optional<UnitedAccount> ua = dto.getCustomer().findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid());

        if (ua.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 UUID로 등록된 통합 계좌가 없습니다.");
        }

        UUID accountOwnerUuid = ua.get().getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) == 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 조회 요청자와 소유자가 다릅니다.");
        }

        Optional<SubAccount> sa = ua.get().findSubAccountByCurrency(dto.getCurrency());

        if (sa.isEmpty()) {
            throw new NoSuchElementException("[ERROR] 해당 UUID로 등록된 환 계좌가 없습니다.");
        }

        sa.get().updateSubAccountStatus(dto.getStatus());
    }

    // 모듈러스 10 알고리즘 참고
    private String generateAccountNumberWithModulus10() {
        String accountNumber = "700";

        long baseNumber = 1_000_000_000L + (long) (new Random().nextDouble() * 9_000_000_000L);

        int checkSum = calculateChecksum(baseNumber);

        return accountNumber + baseNumber + checkSum;
    }

    private int calculateChecksum(long baseNumber) {
        int sum = 0;

        while (baseNumber > 0) {
            sum += baseNumber % 10;
            baseNumber /= 10;
        }
        return 10 - sum % 10;
    }

    private boolean verifyAccountNumber(long number) {
        long baseNumber = number / 10;
        int maybeChecksum = (int) (number % 10);
        return calculateChecksum(baseNumber) == maybeChecksum;
    }
}
