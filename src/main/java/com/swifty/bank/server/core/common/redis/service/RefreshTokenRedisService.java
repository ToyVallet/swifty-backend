package com.swifty.bank.server.core.common.redis.service;

import com.swifty.bank.server.core.common.redis.entity.RefreshTokenCache;

import java.util.concurrent.TimeUnit;

public interface RefreshTokenRedisService {
    RefreshTokenCache getData(String key);

    void setData(String key, RefreshTokenCache value);

    void setData(String key, RefreshTokenCache value, Long timeout, TimeUnit timeUnit);
}
