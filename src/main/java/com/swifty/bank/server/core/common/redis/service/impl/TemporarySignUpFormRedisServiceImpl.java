package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.TemporarySignUpFormRedisRepository;
import com.swifty.bank.server.core.common.redis.service.TemporarySignUpFormRedisService;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporarySignUpFormRedisServiceImpl implements TemporarySignUpFormRedisService {
    public final String prefix = "[TSF]";
    private final TemporarySignUpFormRedisRepository temporarySignUpFormRedisRepository;

    @Value("${jwt.temporary-token-expiration-seconds}")
    private Long timeout;

    @Override
    public TemporarySignUpForm getData(String key) {
        return temporarySignUpFormRedisRepository.getData(prefix + key);
    }

    @Override
    public void setData(String key, TemporarySignUpForm value) {
        temporarySignUpFormRedisRepository.setData(prefix + key, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void setData(String key, TemporarySignUpForm value, Long timeout, TimeUnit timeUnit) {
        temporarySignUpFormRedisRepository.setData(prefix + key, value, timeout, timeUnit);
    }

    @Override
    public boolean deleteData(String key) {
        return temporarySignUpFormRedisRepository.deleteData(prefix + key);
    }
}