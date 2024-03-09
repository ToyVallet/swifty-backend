package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.LogoutAccessTokenRedisRepository;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutAccessTokenRedisRedisServiceImpl implements LogoutAccessTokenRedisService {
    public final String prefix = "[LAT]";
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    private final Long timeout = 5L;

    @Override
    public String getData(String key) {
        return logoutAccessTokenRedisRepository.getData(prefix + key);
    }

    @Override
    public void setData(String key, String value) {
        logoutAccessTokenRedisRepository.setData(prefix + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setData(String key, String value, Long timeout, TimeUnit timeUnit) {
        logoutAccessTokenRedisRepository.setData(prefix + key, value, timeout, timeUnit);
    }

    @Override
    public void setDataIfAbsent(String key, String value) {
        logoutAccessTokenRedisRepository.setDataIfAbsent(prefix + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setDataIfAbsent(String key, String value, Long timeout, TimeUnit timeUnit) {
        logoutAccessTokenRedisRepository.setDataIfAbsent(prefix + key, value, timeout, timeUnit);
    }

    @Override
    public boolean deleteData(String key) {
        return logoutAccessTokenRedisRepository.deleteData(prefix + key);
    }
}
