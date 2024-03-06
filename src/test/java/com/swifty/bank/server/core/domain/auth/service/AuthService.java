package com.swifty.bank.server.core.domain.auth.service;

import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

public class AuthService {
    private AuthenticationService authenticationService;

    @Test
    public void createTemporaryToken( ) {
        String temporaryToken = authenticationService.createTemporaryToken();

        assertThat(temporaryToken.startsWith("ey"));
    }
}
