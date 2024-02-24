package com.swifty.bank.server.core.common.redis.repository;

import com.swifty.bank.server.core.common.redis.entity.RefreshTokenCache;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {
    private final RedisTemplate<String, RefreshTokenCache> redisTemplate;

    public RefreshTokenCache getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, RefreshTokenCache value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
}
