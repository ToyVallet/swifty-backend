package com.swifty.bank.server.config;

import com.swifty.bank.server.core.common.authentication.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost, redisPort);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Auth> redisAuthTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, Auth.class);
    }

    @Bean
    public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, String.class);
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory redisConnectionFactory,
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
