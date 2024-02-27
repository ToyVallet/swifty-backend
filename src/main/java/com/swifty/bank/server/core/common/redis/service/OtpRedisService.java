package com.swifty.bank.server.core.common.redis.service;

import java.util.concurrent.TimeUnit;

public interface OtpRedisService {
    String getData(String key);

    void setData(String key, String value);

    void setData(String key, String value, Long timeout, TimeUnit timeUnit);

    boolean deleteData(String key);
}
