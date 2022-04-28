package com.fanzibang.community.service;

/**
 * redis 操作 service
 */
public interface RedisService {

    void set(String key, Object value);

    void set(String key, Object value, long expire);

    Object get(String key);

    Boolean del(String key);

    Boolean expire(String key, long expire);

    Long getExpire(String key);

}
