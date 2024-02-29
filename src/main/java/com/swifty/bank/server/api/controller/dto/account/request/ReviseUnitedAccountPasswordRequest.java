package com.swifty.bank.server.api.controller.dto.account.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ReviseUnitedAccountPasswordRequest {
    private UUID accountUuid;
    private String password;
}
