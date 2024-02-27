package com.swifty.bank.server.api.controller.dto.account.request;

import com.swifty.bank.server.core.common.constant.Currency;
import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubAccountStatusRequest {
    private UUID unitedAccountUuid;
    private Currency currency;
    private AccountStatus accountStatus;
}
