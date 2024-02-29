package com.swifty.bank.server.core.domain.account.service.impl;

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

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UnitedAccountRepository unitedAccountRepository;
    private final SubAccountRepository subAccountRepository;
    @Override
    public UnitedAccount saveUnitedAccountAndSubAccounts(AccountSaveDto dto) {
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
    public void updateUnitedAccountNickname(AccountNicknameUpdateDto dto) {
        UnitedAccount ua = dto.getCustomer( ).findUnitedAccountByUnitedAccountId(dto.getUaUuid( ));

        UUID accountOwnerUuid = ua.getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer( ).getId( )) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 수정 요청자와 소유자가 다릅니다.");
        }

        ua.updateNickname(dto.getNickname());
    }

    @Override
    public void updateUnitedAccountPassword(AccountPasswordUpdateDto dto) {
        UnitedAccount ua = dto.getCustomer( ).findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid( ));

        UUID accountOwnerUuid = ua.getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 수정 요청자와 소유자가 다릅니다.");
        }

        ua.updatePassword(dto.getPassword());
    }

    @Override
    public double retrieveBalanceByCurrency(RetrieveBalanceOfUnitedAccountByCurrencyDto dto) {
        UnitedAccount ua = dto.getCustomer( ).findUnitedAccountByUnitedAccountId(dto.getUntiedAccountId( ));

        UUID accountOwnerUuid = ua.getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 조회 요청자와 소유자가 다릅니다");
        }

        SubAccount sa = ua.findSubAccountByCurrency(dto.getCurrency( ));

        return sa.getBalance();
    }

    @Override
    public void withdrawUnitedAccount(WithdrawUnitedAccountDto dto) {
        UnitedAccount ua = dto.getCustomer().findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid( ));

        UUID accountOwnerUuid = ua.getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) != 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 조회 요청자와 소유자가 다릅니다.");
        }

        ua.delete();
    }

    @Override
    public void updateUnitedAccountStatus(UpdateUnitedAccountStatusDto dto) {
        UnitedAccount ua = dto.getCustomer().findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid());

        UUID accountOwnerUuid = ua.getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) == 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROr] 계좌의 조회 요청자와 소유자가 다릅니다.");
        }

        ua.updateStatus(dto.getAccountStatus());
    }

    @Override
    public void updateSubAccountStatus(UpdateSubAccountStatusDto dto) {
        UnitedAccount ua = dto.getCustomer().findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid());

        UUID accountOwnerUuid = ua.getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) == 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 조회 요청자와 소유자가 다릅니다.");
        }

        SubAccount sa = ua.findSubAccountByCurrency(dto.getCurrency());

        sa.updateSubAccountStatus(dto.getStatus());
    }

    @Override
    public void updateDefaultCurrency(UpdateDefaultCurrencyDto dto) {
        UnitedAccount ua = dto.getCustomer().findUnitedAccountByUnitedAccountId(dto.getUnitedAccountUuid());

        UUID accountOwnerUuid = ua.getCustomer().getId();

        if (accountOwnerUuid.compareTo(dto.getCustomer().getId()) == 0) {
            throw new RequestorAndOwnerOfUnitedAccountIsDifferentException("[ERROR] 계좌의 조회 요청자와 소유자가 다릅니다.");
        }

        ua.updateDefaultCurrency(dto.getCurrency());
    }

    @Override
    public List<UnitedAccount> listUnitedAccountWithCustomer(ListUnitedAccountWithCustomerDto dto) {
        List<UnitedAccount> unitedAccounts = dto.getCustomer().getUnitedAccounts();

        if (unitedAccounts.size() == 0) {
            throw new NoSuchElementException("[ERROR] 등록된 계좌들이 없습니다.");
        }

        return unitedAccounts;
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
