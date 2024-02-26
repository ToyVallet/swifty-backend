package com.swifty.bank.server.core.common.redis.entity;

import java.util.UUID;

import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenCache extends BaseEntity {
    private String refreshToken;

    public RefreshTokenCache(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}