package com.swifty.bank.server.core.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLoginWithDeviceIDDto {
    private String deviceID;
}
