package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.OtpRedisRepository;
import com.swifty.bank.server.core.common.redis.service.OtpRedisService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpRedisServiceImpl implements OtpRedisService {
    private OtpRedisRepository otpRedisRepository;

    @Value("${jwt.redis.otp-timeout-minutes}")
    private Long timeout;

    @Override
    public String getData(String key) {
        return otpRedisRepository.getData(key);
    }

    @Override
    public void setData(String key, String value) {
        otpRedisRepository.setData(key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setData(String key, String value, Long timeout, TimeUnit timeUnit) {
        otpRedisRepository.setData(key, value, timeout, timeUnit);
    }

    @Override
    public void setDataIfAbsent(String key, String value) {
        otpRedisRepository.setDataIfAbsent(key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setDataIfAbsent(String key, String value, Long timeout, TimeUnit timeUnit) {
        otpRedisRepository.setDataIfAbsent(key, value, timeout, timeUnit);
    }

    @Override
    public boolean deleteData(String key) {
        return otpRedisRepository.deleteData(key);
    }
}