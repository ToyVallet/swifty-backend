package com.swifty.bank.server.core.common.authentication.service.impl;

import com.swifty.bank.server.api.controller.dto.TokenDto;
import com.swifty.bank.server.core.common.authentication.Auth;
import com.swifty.bank.server.core.common.authentication.exception.NoSuchAuthByUuidException;
import com.swifty.bank.server.core.common.authentication.exception.NotLoggedInCustomerException;
import com.swifty.bank.server.core.common.authentication.repository.AuthRepository;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.service.JwtService;
import com.swifty.bank.server.core.common.utils.RedisUtil;
import com.swifty.bank.server.core.domain.customer.Customer;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RedisUtil redisUtil;
    private final JwtService jwtService;
    private final AuthRepository authRepository;


    @Override
    public TokenDto generateTokenDtoWithCustomer(Customer customer) {
        TokenDto tokens = new TokenDto(createAccessToken(customer), createRefreshToken(customer));
        return tokens;
    }

    @Override
    public void logout(UUID uuid) {
        if (!isLoggedOut(uuid)) {
            String key = uuid.toString();
            Auth prevAuth = redisUtil.getRedisAuthValue(key);
            if (prevAuth == null) {
                prevAuth = authRepository.findAuthByUuid(uuid)
                        .orElseThrow(() -> new NoSuchAuthByUuidException("[ERROR] 해당 유저의 로그인 정보가 없습니다."));
            }

            prevAuth.updateAuthContent("LOGOUT");
            redisUtil.saveAuthRedis(key, prevAuth);
        }
        throw new NotLoggedInCustomerException("[ERROR] 로그인 되지 않은 유저가 로그 아웃을 시도했습니다.");
    }

    @Override
    public boolean isLoggedOut(UUID uuid) {
        Auth res = redisUtil.getRedisAuthValue(uuid.toString());
        if (res == null) {
            res = findAuthByUuid(uuid)
                    .orElseThrow(() -> new NoSuchAuthByUuidException("[ERROR] 해당 유저의 로그인 정보가 없습니다."));
            redisUtil.saveAuthRedis(uuid.toString(), res);
        }
        return res.getRefreshToken().equals("LOGOUT");
    }

    @Override
    public Optional<Auth> findAuthByUuid(UUID uuid) {
        return authRepository.findAuthByUuid(uuid);
    }

    private String createAccessToken(Customer customer) {
        return jwtService.createJwtAccessToken(customer.getId());
    }

    private String createRefreshToken(Customer customer) {
        return jwtService.createJwtRefreshToken(customer.getId());
    }

    @Override
    @Transactional
    public void saveRefreshTokenInDataSources(String token) {
        UUID uuid = jwtService.getCustomerId();

        Auth previousAuth = redisUtil.getRedisAuthValue(uuid.toString());
        if (previousAuth == null) {
            previousAuth = findAuthByUuid(uuid)
                    .orElse(null);
        }
        Auth newAuth;

        if (previousAuth != null) {
            previousAuth.updateAuthContent(token);
            newAuth = previousAuth;
        } else {
            newAuth = new Auth(uuid, token);
            authRepository.save(newAuth);
        }
        redisUtil.saveAuthRedis(uuid.toString(), newAuth);
    }
}