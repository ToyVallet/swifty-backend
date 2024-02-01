package com.swifty.bank.server.core.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomerUpdateDeviceIDDto {
    private UUID uuid;
    private String deviceID;
}
