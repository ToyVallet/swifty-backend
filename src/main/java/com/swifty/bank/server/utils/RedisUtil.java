package com.swifty.bank.server.utils;

import com.swifty.bank.server.core.common.authentication.Auth;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Auth> redisTemplate;
    private final RedisTemplate<String, String> redisStringTemplate;

    @Value("${jwt.redis.timeout}")
    private int timeout;

    public Auth getRedisAuthValue(String key) {
        ValueOperations<String, Auth> stringValueOperations = redisTemplate.opsForValue();
        return stringValueOperations.get(key);
    }

    public void saveAuthRedis(String key, Auth value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getRedisStringValue(String key) {
        return redisStringTemplate.opsForValue().get(key);
    }

    public void setRedisStringValue(String key, String value) {
        redisStringTemplate.opsForValue().set(key, value);
    }

    public Boolean setRedisStringExpiration(String key, int timeout, TimeUnit timeUnit) {
        return redisStringTemplate.expire(key, timeout, timeUnit);
    }

    public void deleteRedisStringValue(String key) {
        redisStringTemplate.opsForHash().delete(key);
    }
}