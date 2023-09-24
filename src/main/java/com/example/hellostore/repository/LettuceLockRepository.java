package com.example.hellostore.repository;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public LettuceLockRepository(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean lock(final Long key) {
        return redisTemplate.opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(100_000));
    }

    public void unlock(final Long key) {
        redisTemplate.delete(generateKey(key));
    }

    private String generateKey(final Long key) {
        return key.toString();
    }
}
