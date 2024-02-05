package com.swifty.bank.server.core.common.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth {
    private String refreshToken;
    private boolean isLoggedOut;
}
