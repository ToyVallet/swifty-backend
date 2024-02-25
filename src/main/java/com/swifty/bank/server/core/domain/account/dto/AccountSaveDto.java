package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.common.constant.Bank;
import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSaveDto {
    private Bank bank;
    private String accountPassword;
    private List<Currency> currencies;
    private Currency defaultCurrency;
    private Customer customer;
}
