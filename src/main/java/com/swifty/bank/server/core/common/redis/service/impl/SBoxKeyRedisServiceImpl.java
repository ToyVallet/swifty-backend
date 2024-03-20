package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.SBoxKeyRedisRepository;
import com.swifty.bank.server.core.common.redis.service.SBoxKeyRedisService;
import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SBoxKeyRedisServiceImpl implements SBoxKeyRedisService {
    public final String prefix = "[SK]";
    private final SBoxKeyRedisRepository SBoxKeyRedisRepository;

    private final Long timeout = 3L;

    @Override
    public SBoxKey getData(String key) {
        return SBoxKeyRedisRepository.getData(prefix + key);
    }

    @Override
    public void setData(String key, SBoxKey value) {
        SBoxKeyRedisRepository.setData(prefix + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setData(String key, SBoxKey value, Long timeout, TimeUnit timeUnit) {
        SBoxKeyRedisRepository.setData(prefix + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public boolean deleteData(String key) {
        return SBoxKeyRedisRepository.deleteData(prefix + key);
    }
}