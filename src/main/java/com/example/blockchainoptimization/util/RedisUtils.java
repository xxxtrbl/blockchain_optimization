package com.example.blockchainoptimization.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public final class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void expire(String key, long timeout) throws Exception{
        if(timeout>0){
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } else{
            throw new RuntimeException("Timeout can't be non-positive!");
        }
    }

    public long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    @SuppressWarnings("unchecked")
    public void del(String... key) throws Exception{
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        } else{
            throw new RuntimeException("The key is invalid.");
        }
    }

    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) throws Exception{
        redisTemplate.opsForValue().set(key,value);
    }

    public void set(String key, Object value, long timeout) throws Exception{
        if (timeout > 0){
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } else {
            set (key, value);
        }
    }

    public void hset (String key, String hashKey, Object value) throws Exception{
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object hget(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public void hmset(String key, Map<String, Object> hashMap) throws Exception{
        redisTemplate.opsForHash().putAll(key, hashMap);
    }

    public long hsize(String key) throws Exception{
        return redisTemplate.opsForHash().size(key);
    }

    public Map<Object, Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    public void hset(String key, String item, Object value, Long time) throws Exception{
        if (time > 0){
            expire(key, time);
            redisTemplate.opsForHash().put(key, item, value);
        } else {
            throw new RuntimeException("Expire time can't be negative!");
        }
    }

    public void hdel(String key, Object... hashKeys){
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    public boolean hHasKey(String key, String hashKey){
        return redisTemplate.opsForHash().hasKey(key,hashKey);
    }

    public void zAdd(String key, Object value, Double score){
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public Double zGet(String key, Object value){
        return redisTemplate.opsForZSet().score(key,value);
    }

    public Set zRangeGet(String key, long start, long end){
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public long zRangeCount(String key, long start, long end){
        return redisTemplate.opsForZSet().count(key, start, end);
    }

    public long zTotal(String key){
        return redisTemplate.opsForZSet().size(key);
    }
}
