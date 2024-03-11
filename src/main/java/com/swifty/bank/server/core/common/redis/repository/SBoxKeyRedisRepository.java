package com.swifty.bank.server.core.common.redis.repository;

import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SBoxKeyRedisRepository {
    private final RedisTemplate<String, SBoxKey> redisTemplate;

    public SBoxKey getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, SBoxKey value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public void setDataIfAbsent(String key, SBoxKey value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }

    public boolean deleteData(String key) {
        return redisTemplate.delete(key);
    }
}