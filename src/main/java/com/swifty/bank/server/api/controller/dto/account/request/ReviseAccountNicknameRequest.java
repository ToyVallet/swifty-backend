package com.swifty.bank.server.api.controller.dto.account.request;

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
