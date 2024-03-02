package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.UnitedAccount;
import com.swifty.bank.server.core.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RetrieveBalanceOfUnitedAccountByCurrencyDto {
    private Customer customer;
    private UUID untiedAccountId;
    private Currency currency;
}
