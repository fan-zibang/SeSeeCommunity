package com.fanzibang.community.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Map;
import java.util.Set;

/**
 * redis 操作 service
 */
public interface RedisService {

    <T> T execute(SessionCallback<T> session);

    <T> T execute(RedisCallback<T> action);

    void set(String key, Object value);

    void set(String key, Object value, long expire);

    Object get(String key);

    Boolean del(String key);

    Boolean expire(String key, long expire);

    Long getExpire(String key);

    Boolean hasKey(String key);

    /**
     * 按delta递增
     */
    Long incr(String key, long delta);

    /**
     * 按delta递减
     */
    Long decr(String key, long delta);

    /**
     * 获取Hash结构中的属性
     */
    Object hGet(String key, String hashKey);

    /**
     * 向Hash结构中放入一个属性
     */
    Boolean hSet(String key, String hashKey, Object value, long time);

    /**
     * 向Hash结构中放入一个属性
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 直接获取整个Hash结构
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 直接设置整个Hash结构
     */
    Boolean hSetAll(String key, Map<String, Object> map, long time);

    /**
     * 直接设置整个Hash结构
     */
    void hSetAll(String key, Map<String, ?> map);

    /**
     * 删除Hash结构中的属性
     */
    void hDel(String key, Object... hashKey);

    /**
     * 判断Hash结构中是否有该属性
     */
    Boolean hHasKey(String key, String hashKey);

    /**
     * Hash结构中属性递增
     */
    Long hIncr(String key, String hashKey, Long delta);

    /**
     * Hash结构中属性递减
     */
    Long hDecr(String key, String hashKey, Long delta);

    /**
     * 获取Set结构
     */
    Set<Object> sMembers(String key);

    /**
     * 向Set结构中添加属性
     */
    Long sAdd(String key, Object... values);

    /**
     * 是否为Set中的属性
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 获取Set结构的长度
     */
    Long sSize(String key);

    /**
     * 删除Set结构中的属性
     */
    Long sRemove(String key, Object... values);


    /**
     * 获取Zet结构中该key的分数
     */
    Double zScore (String key, Object value);

    /**
     * 向Zset结构添加属性
     */
    Boolean zAdd(String key, Object value, Double score);

    Long zRemove(String key, Object... value);

    Long zCard(String key);

    Set<Object> zReverseRangeByScore(String key, double min, double max, long offset, long count);

    Long pfAdd(String key, Object... value);

    Long pfCount(String... key);

    Boolean setBit(String key, long offset, boolean value);

}
