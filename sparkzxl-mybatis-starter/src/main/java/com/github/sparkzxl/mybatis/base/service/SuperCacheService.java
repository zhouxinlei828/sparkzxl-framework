package com.github.sparkzxl.mybatis.base.service;

import java.io.Serializable;

/**
 * description: 缓存接口父类
 *
 * @author zhouxinlei
 */
public interface SuperCacheService<T> extends SuperService<T> {

    /**
     * 查询缓存
     *
     * @param var1 id
     * @return T
     */
    T getByIdCache(Serializable var1);

}
