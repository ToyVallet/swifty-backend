package com.swifty.bank.server.core.common.redis.entity;

import java.util.UUID;

import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_refresh_token")
public class RefreshTokenCache extends BaseEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID customerId;
    @Column(columnDefinition = "VARCHAR(1024)")
    private String refreshToken;

    public RefreshTokenCache(UUID customerId, String refreshToken) {
        this.customerId = customerId;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}