package com.swifty.bank.server.core.domain.account.dto;

import com.swifty.bank.server.core.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListUnitedAccountWithCustomerDto {
    private Customer customer;
}
