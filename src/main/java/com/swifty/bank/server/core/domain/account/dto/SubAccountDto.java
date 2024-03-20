package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.SubAccount;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SubAccountDto {
    private Currency currency;

    private double balance;

    private AccountStatus status;

    public static SubAccountDto convertToDto(SubAccount subAccount) {
        return new SubAccountDto(
                subAccount.getCurrency(),
                subAccount.getBalance(),
                subAccount.getStatus()
        );
    }
}
