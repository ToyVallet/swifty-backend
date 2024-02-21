package com.swifty.bank.server.core.common.authentication;

import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth extends BaseEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;
    private String refreshToken;

    public void updateAuthContent(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
