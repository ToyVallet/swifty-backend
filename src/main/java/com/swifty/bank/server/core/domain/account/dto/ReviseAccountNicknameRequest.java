package com.swifty.bank.server.core.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviseAccountNicknameRequest {
    private UUID unitedAccountUuid;
    private String nickname;
}
