package com.swifty.bank.server.core.common.redis.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RefreshTokenCache {
    private String refreshToken;
}