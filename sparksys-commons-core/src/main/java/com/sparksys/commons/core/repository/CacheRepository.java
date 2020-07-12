package com.sparksys.commons.core.repository;

import java.util.function.Function;

/**
 * description: 缓存提供接口
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:25:06
 */
public interface CacheRepository {

    /**
     * 查询缓存
     *
     * @param key 缓存键 不可为空
     * @return T
     * @author zhouxinlei
     * @date 2020-01-27 20:19:27
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
     * @return T
     **/
    <T> T get(String key, Function<String, T> function, Long expireTime);

    /**
     * 查询缓存
     *
     * @param key        缓存键 不可为空
     * @param function   如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam  function函数的调用参数
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @return T
     **/
    <T, M> T get(String key, Function<M, T> function, M funcParam, Long expireTime);

    /**
     * 设置缓存键值
     *
     * @param key 缓存键 不可为空
     * @param obj 缓存值 不可为空
     * @return void
     **/
    void set(String key, Object obj);

    /**
     * 设置缓存键值
     *
     * @param key        缓存键 不可为空
     * @param value      缓存值 不可为空
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @return void
     **/
    void set(String key, Object value, Long expireTime);

    /**
     * 自增长
     *
     * @param key   key值
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
     * @param key   key值
     * @return Long
     * @author zhouxinlei
     * @date 2019-10-11 16:23:58
     */
    Long decrement(String key);

    /**
     * 自减
     *
     * @param key   key值
     * @param delta 自减间距
     * @return Long
     * @author zhouxinlei
     * @date 2019-10-11 16:23:58
     */
    Long decrement(String key, long delta);

    /**
     * 移除缓存
     *
     * @param keys 缓存键 不可为空
     * @return Long
     */
    Long remove(String... keys);

    /**
     * 是否存在缓存
     *
     * @param key 缓存键 不可为空
     * @return boolean
     **/
    boolean exists(String key);

    /**
     * 刷新DB
     *
     * @return void
     **/
    void flushDb();


}
