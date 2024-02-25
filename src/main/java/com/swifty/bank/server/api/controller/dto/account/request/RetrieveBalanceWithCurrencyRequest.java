package com.swifty.bank.server.api.controller.dto.account.request;

import com.swifty.bank.server.core.common.constant.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class RetrieveBalanceWithCurrencyRequest {
    private UUID unitedAccountUuid;
    private Currency currency;
}
