package com.swifty.bank.server.core.customer.dto;

import com.swifty.bank.server.core.customer.constant.Nationality;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomerJoinDto {
    private UUID uuid;
    private String name;
    private Nationality nationality;
    private String phoneNumber;
}