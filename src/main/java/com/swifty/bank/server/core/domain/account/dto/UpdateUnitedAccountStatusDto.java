package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.domain.account.constant.AccountStatus;
import com.swifty.bank.server.core.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUnitedAccountStatusDto {
    private Customer customer;
    private UUID unitedAccountUuid;
    private AccountStatus accountStatus;
}
