package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomerUpdatePhoneNumberAndNationalityDto {
    private UUID uuid;
    private Nationality nationality;
    private String phoneNumber;
}
