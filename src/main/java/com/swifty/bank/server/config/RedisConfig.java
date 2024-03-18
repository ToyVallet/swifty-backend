package com.swifty.bank.server.config;

import com.swifty.bank.server.core.common.redis.value.SBoxKey;
import com.swifty.bank.server.core.common.redis.value.TemporarySignUpForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("redis host={}",redisHost);
        log.info("redis port={}",redisPort);
        log.info("mysql url={}",mysqlUrl);
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, TemporarySignUpForm> temporarySignUpFormRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, TemporarySignUpForm.class);
    }

    @Bean
    public RedisTemplate<String, SBoxKey> secureKeypadIndexesRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, SBoxKey.class);
    }

    public <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                                            Class<T> type) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(type);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
}