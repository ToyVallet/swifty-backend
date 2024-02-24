package com.swifty.bank.server.core.common.authentication;

import com.swifty.bank.server.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "tb_auth")
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class Auth extends BaseEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;
    private String refreshToken;

    public void updateAuthContent(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
