package com.swifty.bank.server.src.main.core.customer.dto;

import com.swifty.bank.server.src.main.core.customer.constant.Nationality;
import lombok.Getter;

@Getter
public class CustomerJoinDto {
    private String name;
    private Nationality nationality;
    private String phoneNumber;
}