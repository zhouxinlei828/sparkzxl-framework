package com.sparksys.commons.mybatis.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.sparksys.commons.core.constant.CacheKey;
import com.sparksys.commons.core.repository.CacheRepository;
import com.sparksys.commons.mybatis.entity.SuperEntity;
import com.sparksys.commons.mybatis.mapper.SuperMapper;
import com.sparksys.commons.mybatis.service.SuperCacheService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

/**
 * description: 缓存接口父类实现类
 *
 * @author: zhouxinlei
 * @date: 2020-07-07 19:38:37
 */
public abstract class AbstractSuperCacheServiceImpl<M extends SuperMapper<T>, T> extends SuperServiceImpl<M, T> implements SuperCacheService<T> {

    @Autowired(required = false)
    protected CacheRepository cacheRepository;

    protected abstract String getRegion();

    @Override
    public T getByIdCache(Serializable id) {
        return this.cacheRepository.get(this.getRegion(), (x) -> super.getById(id));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        this.cacheRepository.remove(CacheKey.buildKey(this.getRegion(), id));
        return bool;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        } else {
            boolean flag = super.removeByIds(idList);
            idList.forEach(id -> this.cacheRepository.remove(CacheKey.buildKey(this.getRegion(), id)));
            return flag;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean save(T model) {
        boolean result = super.save(model);
        if (model instanceof SuperEntity) {
            String key = CacheKey.buildKey(this.getRegion(), ((SuperEntity) model).getId());
            this.cacheRepository.set(key, model);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        if (model instanceof SuperEntity) {
            this.cacheRepository.remove(CacheKey.buildKey(this.getRegion(), ((SuperEntity) model).getId()));
        }
        return updateBool;
    }
}
