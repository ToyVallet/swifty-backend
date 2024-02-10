package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.exception.TokenContentNotValidException;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.utils.JwtTokenUtil;
import com.swifty.bank.server.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RedisUtil redisUtil;
    private final JwtTokenUtil jwtTokenUtil;

    private void saveRefreshTokenInRedis(String token) {
        UUID uuid = jwtTokenUtil.getUuidFromToken(token);
        Auth previousAuth = redisUtil.getRedisAuthValue(uuid.toString());
        Auth newAuth;

        if (previousAuth != null && !previousAuth.getRefreshToken().isEmpty()) {
            newAuth = new Auth(token, previousAuth.isLoggedOut());
            UUID prevUuid = jwtTokenUtil.getUuidFromToken(previousAuth.getRefreshToken());

            if (!uuid.toString().equals(prevUuid.toString())) {
                throw new TokenContentNotValidException("[ERROR] Two token's owner is different");
            }
        } else {
            newAuth = new Auth(token, false);
        }
        redisUtil.saveAuthRedis(uuid.toString(), newAuth);
    }

    @Override
    public TokenDto generateTokenWithCustomer(Customer customer) {
        TokenDto tokens = jwtTokenUtil.generateToken(customer);
        saveRefreshTokenInRedis(tokens.getRefreshToken());
        return tokens;
    }
}
