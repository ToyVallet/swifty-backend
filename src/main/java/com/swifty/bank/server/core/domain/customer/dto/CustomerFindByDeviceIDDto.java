package com.swifty.bank.server.core.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerFindByDeviceIDDto {
    private String deviceID;
}
