package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.TemporarySignUpFormRepository;
import com.swifty.bank.server.core.common.redis.service.TemporarySignUpFormRedisService;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporarySignUpFormRedisServiceImpl implements TemporarySignUpFormRedisService {
    private final TemporarySignUpFormRepository temporarySignUpFormRepository;

    @Value("${jwt.redis.temporary-token-minutes}")
    private Long timeout;

    @Override
    public TemporarySignUpForm getData(String key) {
        return temporarySignUpFormRepository.getData(key);
    }

    @Override
    public void setData(String key, TemporarySignUpForm value) {
        temporarySignUpFormRepository.setData(key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setData(String key, TemporarySignUpForm value, Long timeout, TimeUnit timeUnit) {
        temporarySignUpFormRepository.setData(key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public boolean deleteData(String key) {
        return temporarySignUpFormRepository.deleteData(key);
    }
}