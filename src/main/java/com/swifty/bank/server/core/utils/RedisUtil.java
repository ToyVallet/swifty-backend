//package com.swifty.bank.server.core.common.utils;
//
//import com.swifty.bank.server.core.common.redis.repository.OtpRedisRepository;
//import com.swifty.bank.server.core.common.redis.repository.RefreshTokenRedisRepository;
//import java.util.concurrent.TimeUnit;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//@Component
//@RequiredArgsConstructor
//public class RedisUtil {
//    private final OtpRedisRepository;
//    private final RefreshTokenRedisRepository;
//
//    public void setData(String key, String value) {
//        redisTemplate.opsForValue().set(key, value);
//    }
//
//    public void setData(String key, String value, Long timeout, TimeUnit timeUnit) {
//        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
//    }
//
//    public String getData(String key) {
//        return (String) redisTemplate.opsForValue().get(key);
//    }
//
//    public void deleteData(String key) {
//        redisTemplate.delete(key);
//    }
//}