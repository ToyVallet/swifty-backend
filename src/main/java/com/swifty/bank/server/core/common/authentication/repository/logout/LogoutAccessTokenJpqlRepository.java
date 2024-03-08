package com.swifty.bank.server.core.common.authentication.repository.logout;

import com.swifty.bank.server.core.common.authentication.LogoutAccessToken;

import java.util.Optional;

public interface LogoutAccessTokenJpqlRepository {
    public Optional<LogoutAccessToken> findSingleLogoutAccessTokenWithAccessToken(String accessToken);
}
