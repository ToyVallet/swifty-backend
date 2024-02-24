package com.swifty.bank.server.core.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountNicknameUpdateDto {
    private UUID customerUuid;
    private UUID uaUuid;
    private String nickname;
}
