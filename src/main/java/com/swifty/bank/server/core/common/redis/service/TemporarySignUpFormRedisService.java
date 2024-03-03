package com.swifty.bank.server.core.common.redis.service;

import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import java.util.concurrent.TimeUnit;

public interface TemporarySignUpFormRedisService {
    TemporarySignUpForm getData(String key);

    void setData(String key, TemporarySignUpForm value);

    void setData(String key, TemporarySignUpForm value, Long timeout, TimeUnit timeUnit);
}