package com.swifty.bank.server.core.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerFindByPhoneNumberDto {
    private String phoneNumber;
}
