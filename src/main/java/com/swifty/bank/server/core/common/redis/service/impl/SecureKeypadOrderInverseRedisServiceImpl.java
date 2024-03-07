package com.swifty.bank.server.core.common.redis.service.impl;

import com.swifty.bank.server.core.common.redis.repository.SecureKeypadOrderInverseRedisRepository;
import com.swifty.bank.server.core.common.redis.service.SecureKeypadOrderInverseRedisService;
import com.swifty.bank.server.core.common.redis.value.SecureKeypadOrderInverse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecureKeypadOrderInverseRedisServiceImpl implements SecureKeypadOrderInverseRedisService {
    public final String prefix = "[SK]";
    private final SecureKeypadOrderInverseRedisRepository secureKeypadOrderInverseRedisRepository;

    private final Long timeout = 3L;

    @Override
    public SecureKeypadOrderInverse getData(String key) {
        return secureKeypadOrderInverseRedisRepository.getData(prefix + key);
    }

    @Override
    public void setData(String key, SecureKeypadOrderInverse value) {
        secureKeypadOrderInverseRedisRepository.setData(prefix + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setData(String key, SecureKeypadOrderInverse value, Long timeout, TimeUnit timeUnit) {
        secureKeypadOrderInverseRedisRepository.setData(prefix + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public boolean deleteData(String key) {
        return secureKeypadOrderInverseRedisRepository.deleteData(prefix + key);
    }
}