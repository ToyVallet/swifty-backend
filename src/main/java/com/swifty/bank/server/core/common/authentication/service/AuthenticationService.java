package com.swifty.bank.server.core.common.authentication.service;

import com.swifty.bank.server.core.common.authentication.RefreshTokenDb;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.domain.customer.Customer;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AuthenticationService {
    TokenDto generateTokenDto(Customer customer);

    void logout(UUID uuid);

    boolean isLoggedOut(UUID uuid);

    Optional<RefreshTokenDb> findAuthByCustomerId(UUID uuid);

    void saveRefreshTokenInDataSources(String token);

    String createAccessToken(Customer customer);

    String createRefreshToken(Customer customer);

    Map<String, Object> generateAndStoreRefreshToken(Customer customerByPhoneNumber);
}
