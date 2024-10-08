package com.minyan.nascommon.service;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/6 17:14
 */
@Service
public class RedisService {
  public static final int DEFAULT_LOCK_SECONDS = 30;
  @Autowired private RedisTemplate<String, Object> redisTemplate;

  public void set(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  public void setExpire(String key, Object value, long timeout) {
    redisTemplate.opsForValue().set(key, value, timeout);
  }

  public boolean lock(String key, String value, long expire) {
    return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(expire));
  }

  public boolean lock(String key) {
    return redisTemplate
        .opsForValue()
        .setIfAbsent(key, "lock", Duration.ofSeconds(DEFAULT_LOCK_SECONDS));
  }

  public void releaseLock(String key) {
    redisTemplate.delete(key);
  }
}
