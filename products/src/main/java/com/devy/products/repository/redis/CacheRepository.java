package com.devy.products.repository.redis;

import org.springframework.stereotype.Repository;

@Repository
public interface CacheRepository {

    public void save(String key, Object value);
    public String getValue(String key);
    public <T> T getValue(String key, Class<T> clazz);
    public boolean getLock(String key);
    public boolean releaseLock(String key);
    public void delete(String key);
}
