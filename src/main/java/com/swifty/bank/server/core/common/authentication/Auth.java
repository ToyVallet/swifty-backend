package com.swifty.bank.server.core.common.authentication;

import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "tb_ref_token")
public class Auth extends BaseEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID customerUuid;
    @Column(columnDefinition = "VARCHAR(1024)")
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
