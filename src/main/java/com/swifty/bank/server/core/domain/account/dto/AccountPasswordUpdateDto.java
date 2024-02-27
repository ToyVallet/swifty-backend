package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountPasswordUpdateDto {
    private Customer customer;
    private UUID unitedAccountUuid;
    private String password;
}
