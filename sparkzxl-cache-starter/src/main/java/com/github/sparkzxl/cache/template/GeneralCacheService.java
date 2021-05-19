package com.github.sparkzxl.cache.template;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * description: 缓存提供接口
 *
 * @author zhouxinlei
 */
public interface GeneralCacheService {

    /**
     * 查询缓存
     *
     * @param key 缓存键 不可为空
     * @return T
     */
    <T> T get(String key);

    /**
     * 查询缓存
     *
     * @param key      缓存键 不可为空
     * @param function 如没有缓存，调用该callable函数返回对象 可为空
     * @return T
     */
    <T> T get(String key, Function<String, T> function);

    /**
     * 查询缓存
     *
     * @param key       缓存键 不可为空
     * @param function  如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam function函数的调用参数
     * @return T
     */
    <T, M> T get(String key, Function<M, T> function, M funcParam);

    /**
     * 查询缓存
     *
     * @param key        缓存键 不可为空
     * @param function   如没有缓存，调用该callable函数返回对象 可为空
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @param timeUnit   java.util.concurrent.TimeUnit
     * @return T
     * @see TimeUnit
     **/
    <T> T get(String key, Function<String, T> function, Long expireTime, TimeUnit timeUnit);

    /**
     * 查询缓存
     *
     * @param key        缓存键 不可为空
     * @param function   如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam  function函数的调用参数
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @param timeUnit   java.util.concurrent.TimeUnit
     * @return T
     * @see TimeUnit
     **/
    <T, M> T get(String key, Function<M, T> function, M funcParam, Long expireTime, TimeUnit timeUnit);

    /**
     * 设置缓存键值
     *
     * @param key 缓存键 不可为空
     * @param obj 缓存值 不可为空
     **/
    void set(String key, Object obj);

    /**
     * 设置缓存键值
     *
     * @param key        缓存键 不可为空
     * @param value      缓存值 不可为空
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @param timeUnit   java.util.concurrent.TimeUnit
     * @see TimeUnit
     **/
    void set(String key, Object value, Long expireTime, TimeUnit timeUnit);

    /**
     * 不存在时设置缓存键值对
     *
     * @param key        键值
     * @param value      value值
     * @param expireTime 过期时间（单位：毫秒）
     * @param timeUnit   java.util.concurrent.TimeUnit
     * @return boolean
     */
    boolean setIfAbsent(String key, Object value, Long expireTime, TimeUnit timeUnit);

    /**
     * 不存在时设置缓存键值对
     *
     * @param key   键值
     * @param value value值
     * @return boolean
     */
    boolean setIfAbsent(String key, Object value);

    /**
     * 自增长
     *
     * @param key key值
     * @return Long
     */
    Long increment(String key);

    /**
     * 自增长
     *
     * @param key   key值
     * @param delta 自增间距
     * @return Long
     */
    Long increment(String key, long delta);

    /**
     * 自减
     *
     * @param key key值
     * @return Long
     */
    Long decrement(String key);

    /**
     * 自减
     *
     * @param key   key值
     * @param delta 自减间距
     * @return Long
     */
    Long decrement(String key, long delta);

    /**
     * 移除缓存
     *
     * @param keys 缓存键 不可为空
     */
    void remove(String... keys);

    /**
     * 是否存在缓存
     *
     * @param key 缓存键 不可为空
     * @return boolean
     **/
    boolean exists(String key);

    /**
     * 刷新DB
     **/
    void flushDb();


}
