package com.swifty.bank.server.core.common.authentication.service;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.ExpiredRefToken;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.domain.customer.Customer;

import java.util.Optional;
import java.util.UUID;

public interface AuthenticationService {
    TokenDto generateTokenDtoWithCustomer(Customer customer);

    void logout(UUID uuid);

    boolean isLoggedOut(UUID uuid);

    Optional<Auth> findAuthByUuid(UUID uuid);

    Optional<ExpiredRefToken> findExpiredTokenByRefreshToken(String refreshToken);

    void saveRefreshTokenInDataSources(String token);
}
