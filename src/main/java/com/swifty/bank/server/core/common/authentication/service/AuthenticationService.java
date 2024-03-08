package com.swifty.bank.server.core.common.authentication.service;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.LogoutAccessToken;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import java.util.Optional;
import java.util.UUID;

public interface AuthenticationService {
    TokenDto generateTokenDto(UUID customerUuid);

    void deleteAuth(UUID customerUuid);

    Optional<Auth> findAuthByCustomerUuid(UUID customerUuid);

    Optional<LogoutAccessToken> saveLogoutAccessToken(String accessToken);

    Optional<LogoutAccessToken> findLogoutAccessToken(String accessToken);

    void deleteLogoutAccessToken(String accessToken);

    void updateLogoutAccessToken(String accessToken);

    void saveRefreshTokenInDatabase(String token);

    String createAccessToken(UUID customerUuid);

    String createRefreshToken(UUID customerUuid);

    String createTemporaryToken();
}