package com.swifty.bank.server.core.common.redis.repository;

import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TemporarySignUpFormRepository {
    private final RedisTemplate<String, TemporarySignUpForm> redisTemplate;

    public TemporarySignUpForm getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, TemporarySignUpForm value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public void setDataIfAbsent(String key, TemporarySignUpForm value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }
}