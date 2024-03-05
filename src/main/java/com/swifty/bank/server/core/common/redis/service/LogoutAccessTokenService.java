package com.swifty.bank.server.core.common.redis.service;

import java.util.concurrent.TimeUnit;

public interface LogoutAccessTokenService {

    String getData(String key);

    void setData(String key, String value);

    void setData(String key, String value, Long timeout, TimeUnit timeUnit);

    void setDataIfAbsent(String key, String value);

    void setDataIfAbsent(String key, String value, Long timeout, TimeUnit timeUnit);

    boolean deleteData(String key);
}
