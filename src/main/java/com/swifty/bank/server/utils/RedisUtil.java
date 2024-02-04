package com.swifty.bank.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${jwt.redis.timeout}")
    private int timeout;

    public String getRedisValue(String key) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue( );
        return stringValueOperations.get(key);
    }

    public void saveRedis(String key, String value) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue( );
        stringValueOperations.set(key, value, timeout, TimeUnit.HOURS);
    }
}
