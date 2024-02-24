package com.swifty.bank.server.core.domain.account.service.impl;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.SubAccount;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.dto.AccountNicknameUpdateDto;
import com.swifty.bank.server.core.domain.account.dto.AccountSaveDto;
import com.swifty.bank.server.core.domain.account.exception.NoSuchUnitedAccountByUuidException;
import com.swifty.bank.server.core.domain.account.exception.RequestorAndOwnerOfUnitedAccountIsDifferentException;
import com.swifty.bank.server.core.domain.account.repository.SubAccountRepository;
import com.swifty.bank.server.core.domain.account.repository.UnitedAccountRepository;
import com.swifty.bank.server.core.domain.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .bank(dto.getBank())
                .customer(dto.getCustomer())
                .build();

        UnitedAccount createdAccount = unitedAccountRepository.save(ua);

        for (Currency cur : dto.getCurrencies()) {
            SubAccount sa = SubAccount.builder()
                    .ua(createdAccount)
                    .cur(cur)
                    .build();

            subAccountRepository.save(sa);
        }

        return createdAccount;
    }

    @Override
    public void updateUaNickname(AccountNicknameUpdateDto dto) {
        Optional<UnitedAccount> ua = unitedAccountRepository.findByUuid(dto.getUaUuid());

        if (ua.isEmpty()) {
            throw new NoSuchUnitedAccountByUuidException("[ERROR] 해당 UUID로 등록된 통합 계좌가 없습니다.");
        }

        UUID accountOwnerUuid = ua.get().getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomerUuid()) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 수정 요청자와 소유자가 다릅니다.");
        }

        ua.get().updateNickname(dto.getNickname());
    }

    // 모듈러스 10 알고리즘 참고
    private String generateAccountNumberWithModulus10() {
        String accountNumber = "700";

        long baseNumber = 1_000_000_000L + (long) (new Random().nextDouble() * 9_000_000_000L);

        int checkSum = calculateChecksum(baseNumber);

        return String.valueOf(baseNumber) + checkSum;
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
