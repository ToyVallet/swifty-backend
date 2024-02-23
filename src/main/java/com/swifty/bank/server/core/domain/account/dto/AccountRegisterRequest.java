package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.common.constant.Bank;
import com.swifty.bank.server.core.common.constant.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRegisterRequest {
    private Bank bank;
    private String accountPassword;
    private List<Currency> currencies;
}
