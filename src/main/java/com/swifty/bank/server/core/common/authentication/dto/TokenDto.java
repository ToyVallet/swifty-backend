package com.swifty.bank.server.core.common.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
}
