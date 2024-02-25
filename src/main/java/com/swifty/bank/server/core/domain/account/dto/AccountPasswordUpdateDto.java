package com.swifty.bank.server.core.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountPasswordUpdateDto {
    private UUID customerUuid;
    private UUID unitedAccountUuid;
    private String password;
}
