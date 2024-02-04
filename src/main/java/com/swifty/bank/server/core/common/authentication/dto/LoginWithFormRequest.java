package com.swifty.bank.server.core.common.authentication.dto;

import lombok.Getter;

@Getter
public class LoginWithFormRequest {
    private String phoneNumber;
    private String deviceId;
}
