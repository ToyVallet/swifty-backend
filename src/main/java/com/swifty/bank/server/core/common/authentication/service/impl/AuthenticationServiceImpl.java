package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.core.common.authentication.dto.TokenDto;
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
        String previousRefreshToken = redisUtil.getRedisValue(uuid.toString());
        if (previousRefreshToken != null || !previousRefreshToken.isEmpty()) {
            UUID prevUuid = jwtTokenUtil.getUuidFromToken(previousRefreshToken);
            redisUtil.saveRedis(previousRefreshToken, "Refresh Token Deprecated: " + prevUuid.toString());
        }
        redisUtil.saveRedis(uuid.toString(), token);
    }

    @Override
    public TokenDto generateTokenWithCustomer(Customer customer) {
        TokenDto tokens = jwtTokenUtil.generateToken(customer);
        saveRefreshTokenInRedis(tokens.getRefreshToken());
        return tokens;
    }
}
