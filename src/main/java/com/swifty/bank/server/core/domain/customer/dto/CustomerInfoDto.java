package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.CustomerStatus;
import com.swifty.bank.server.core.domain.customer.constant.Gender;
import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoDto {
    private String name;
    private String phoneNumber;
    private Gender gender;
    private String birthDate;
    private Nationality nationality;
    private CustomerStatus customerStatus;
}