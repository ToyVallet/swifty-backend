package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.OtpRedisRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpRedisServiceImpl {
    private final OtpRedisRepository otpRedisRepository;

    public String getData(String key) {
        return otpRedisRepository.getData(key);
    }

    public void setData(String key, String value) {
        otpRedisRepository.setData(key, value, 10L, TimeUnit.MINUTES);
    }

    public void setData(String key, String value, Long timeout, TimeUnit timeUnit) {
        otpRedisRepository.setData(key, value, timeout, timeUnit);
    }

    public boolean deleteData(String key) {
        return otpRedisRepository.deleteData(key);
    }
}