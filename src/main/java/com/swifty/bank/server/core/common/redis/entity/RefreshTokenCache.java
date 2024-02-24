package com.swifty.bank.server.core.common.redis.entity;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenCache {
    private UUID customerId;
    private String refreshToken;

    public RefreshTokenCache(UUID customerId, String refreshToken) {
        this.customerId = customerId;
        this.refreshToken = refreshToken;
    }
}