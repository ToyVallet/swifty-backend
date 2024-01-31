package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerFindByPhoneNumberAndNationality {
    private String phoneNumber;
    private Nationality nationality;
}
