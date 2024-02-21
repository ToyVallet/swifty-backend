package com.swifty.bank.server.core.common.authentication;

import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_expired_refresh_token")
public class ExpiredRefToken extends BaseEntity {
    @Id
    String refreshToken;
    UUID uuid;
}
