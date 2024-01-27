package com.swifty.bank.server.core.customer.dto;

import com.swifty.bank.server.core.customer.constant.Nationality;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomerJoinDto {
    private String name;
    private Nationality nationality;
    private String bod;
    private String sex;
    private String password;
    private String phoneNumber;
    private String deviceId;
}