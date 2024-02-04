package com.swifty.bank.server.core.domain.customer.dto;

import com.swifty.bank.server.core.domain.customer.constant.Nationality;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class JoinRequest {
    private UUID uuid;
    private String name;
    private Nationality nationality;
    private String phoneNumber;
    private String password;
    private String deviceId;
}