package com.github.sparkzxl.mybatis.base.service;

import com.github.sparkzxl.core.entity.cache.CacheKey;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * description: 缓存接口父类
 *
 * @author zhouxinlei
 */
public interface SuperCacheService<T> extends SuperService<T>, CacheOperationService<T> {


    /**
     * 可能会缓存穿透
     *
     * @param ids    主键id
     * @param loader 回调
     * @return 对象集合
     */
    List<T> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<T>> loader);


}
