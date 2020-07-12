package com.sparksys.commons.mybatis.service;

import java.io.Serializable;

/**
 * description: 缓存接口父类
 *
 * @author: zhouxinlei
 * @date: 2020-07-07 19:30:28
 */
public interface SuperCacheService<T> extends SuperService<T> {

    /**
     * 查询缓存
     *
     * @param var1
     * @return
     */
    T getByIdCache(Serializable var1);

}
