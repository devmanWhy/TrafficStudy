package com.devy.products.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Repository
public class RedisRepository implements CacheRepository {

    public static final String PRODUCT_PREFIX = "product:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String key, Object value) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, Duration.ofMinutes(5)); // 5분 후 만료
    }

    @Override
    public String getValue(String key) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        return (String) ops.get(key);
    }

    @Override
    public <T> T getValue(String key, Class<T> clazz) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object value = ops.get(key);
        if (value == null) return null;
        return objectMapper.convertValue(value, clazz);
    }
}
