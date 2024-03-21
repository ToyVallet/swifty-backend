package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.SBoxKeyRedisRepository;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SBoxKeyRedisServiceImpl implements SBoxKeyRedisService {
    public final String prefix = "[SK]";
    private final SBoxKeyRedisRepository SBoxKeyRedisRepository;

    @Value("${jwt.keypad-token-expiration-seconds}")
    private Long timeout;

    @Override
    public SBoxKey getData(String key) {
        return SBoxKeyRedisRepository.getData(prefix + key);
    }

    @Override
    public void setData(String key, SBoxKey value) {
        SBoxKeyRedisRepository.setData(prefix + key, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void setData(String key, SBoxKey value, Long timeout, TimeUnit timeUnit) {
        SBoxKeyRedisRepository.setData(prefix + key, value, timeout, timeUnit);
    }

    @Override
    public boolean deleteData(String key) {
        return SBoxKeyRedisRepository.deleteData(prefix + key);
    }
}