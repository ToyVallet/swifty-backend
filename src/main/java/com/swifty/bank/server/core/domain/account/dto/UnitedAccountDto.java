package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.common.constant.ProductType;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import com.swifty.bank.server.core.domain.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class UnitedAccountDto {
    private UUID unitedAccountUuid;
    private String accountNumber;
    private Product product;
    private String accountPassword;
    private String nickname;
    private Currency defaultCurrency;
    private AccountStatus status;
    private List<SubAccountDto> subAccounts;

    public UnitedAccountDto(UnitedAccount unitedAccount) {
        this.unitedAccountUuid = unitedAccount.getUnitedAccountUuid();
        this.accountNumber = unitedAccount.getAccountNumber();
        this.product = unitedAccount.getProduct();
        this.accountPassword = unitedAccount.getAccountPassword();
        this.nickname = unitedAccount.getNickname();
        this.defaultCurrency = unitedAccount.getDefaultCurrency();
        this.status = unitedAccount.getStatus();
        this.subAccounts = unitedAccount.getSubAccounts()
                .stream()
                .map(subAccount -> SubAccountDto.convertToDto(subAccount))
                .toList();
    }
}
