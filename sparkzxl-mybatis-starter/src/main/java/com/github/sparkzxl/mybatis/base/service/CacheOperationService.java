package com.github.sparkzxl.mybatis.base.service;

import com.github.sparkzxl.core.entity.cache.CacheKey;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * description: 缓存接口父类
 *
 * @author zhouxinlei
 */
public interface CacheOperationService<T> {

    /**
     * 根据id 先查缓存，再查db
     *
     * @param id 主键
     * @return 对象
     */
    T getByIdCache(Serializable id);

    /**
     * 根据 key 查询缓存中存放的id，缓存不存在根据loader加载并写入数据，然后根据查询出来的id查询 实体
     *
     * @param key    缓存key
     * @param loader 加载器
     * @return 对象
     */
    T getByKey(CacheKey key, Function<CacheKey, Object> loader);

    /**
     * 根据 key 查询缓存中存放的id，缓存不存在根据loader加载并写入数据，然后根据查询出来的id查询 实体
     *
     * @param key    缓存key
     * @param loader 加载器
     * @return 对象
     */
    List<T> listByKey(CacheKey key, Function<CacheKey, Set<Object>> loader);

    /**
     * 设置缓存
     *
     * @param model 对象
     */
    void setCache(T model);

    /**
     * 根据ids删除缓存
     *
     * @param ids ids
     */
    void delCache(Serializable... ids);

    /**
     * 根据缓存key删除缓存
     *
     * @param key 缓存key
     */
    void delCache(CacheKey key);

    /**
     * 根据对象删除缓存
     *
     * @param model 对象
     */
    void delCache(T model);

    /**
     * 根据id集合删除缓存
     *
     * @param idList id集合
     */
    void delCache(Collection<?> idList);

    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 清理缓存
     */
    void clearCache();

}
