package com.swifty.bank.server.core.common.redis.service;

import com.swifty.bank.server.core.common.redis.value.SecureKeypadOrderInverse;
import java.util.concurrent.TimeUnit;

public interface SecureKeypadOrderInverseRedisService {
    SecureKeypadOrderInverse getData(String key);

    void setData(String key, SecureKeypadOrderInverse value);

    void setData(String key, SecureKeypadOrderInverse value, Long timeout, TimeUnit timeUnit);

    boolean deleteData(String key);
}
