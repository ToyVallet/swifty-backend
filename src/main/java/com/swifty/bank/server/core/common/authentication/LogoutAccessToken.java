package com.swifty.bank.server.core.common.authentication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_logout_access_token")
@Entity
@Getter
public class LogoutAccessToken {
    @Column(columnDefinition = "VARCHAR(1024)")
    @Id
    private String accessToken;
    private String isLoggedIn;

    public void updateIsLoggedIn(String isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}
