package com.swifty.bank.server.api.controller.dto.account.request;

import com.swifty.bank.server.core.common.constant.Product;
import com.swifty.bank.server.core.common.constant.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRegisterRequest {
    private Product product;
    private String accountPassword;
    private List<Currency> currencies;
    private Currency defaultCurrency;
    private int registerLimit;
}