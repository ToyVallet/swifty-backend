package com.swifty.bank.server.core.common.redis.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OtpRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, String value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public boolean deleteData(String key) {
        return redisTemplate.delete(key);
    }
}
