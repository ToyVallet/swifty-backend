package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.entity.RefreshTokenCache;
import com.swifty.bank.server.core.common.redis.repository.RefreshTokenRedisRepository;
import com.swifty.bank.server.core.common.redis.service.RedisService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisServiceImpl implements RedisService {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${jwt.redis.timeout}")
    private Long timeout;

    public RefreshTokenCache getData(String key) {
        return refreshTokenRedisRepository.getData(key);
    }

    public void setData(String key, RefreshTokenCache value) {
        refreshTokenRedisRepository.setData(key, value, timeout, TimeUnit.HOURS);
    }

    public void setData(String key, RefreshTokenCache value, Long timeout, TimeUnit timeUnit) {
        refreshTokenRedisRepository.setData(key, value, timeout, timeUnit);
    }
}