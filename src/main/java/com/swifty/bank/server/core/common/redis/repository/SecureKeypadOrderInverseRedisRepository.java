package com.swifty.bank.server.core.common.redis.repository;

import com.swifty.bank.server.core.common.redis.value.SecureKeypadOrderInverse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecureKeypadOrderInverseRedisRepository {
    private final RedisTemplate<String, SecureKeypadOrderInverse> redisTemplate;

    public SecureKeypadOrderInverse getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, SecureKeypadOrderInverse value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public void setDataIfAbsent(String key, SecureKeypadOrderInverse value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }

    public boolean deleteData(String key) {
        return redisTemplate.delete(key);
    }
}