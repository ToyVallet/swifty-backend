package com.swifty.bank.server.core.common.redis.service;

import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import java.util.concurrent.TimeUnit;

public interface SBoxKeyRedisService {
    SBoxKey getData(String key);

    void setData(String key, SBoxKey value);

    void setData(String key, SBoxKey value, Long timeout, TimeUnit timeUnit);

    boolean deleteData(String key);
}
