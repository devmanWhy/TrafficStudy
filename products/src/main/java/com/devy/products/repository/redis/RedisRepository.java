package com.devy.products.repository.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository implements CacheRepository {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String PRODUCT_PREFIX = "product:";
    private static final String LOCK_PREFIX = "lock:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.redissonClient = redissonClient;
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

    @Override
    public boolean getLock(String key) {
        try {
            RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
            return lock.tryLock(2, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Lock 획득 중 오류 발생 {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean releaseLock(String key) {
        try {
            RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
            lock.unlock();
            log.info("락 해제 완료 : {}", key);
            return true;
        } catch (Exception e) {
            log.error("Unlock 중 오류 발생, {}", e.getMessage());
        }
        return false;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
