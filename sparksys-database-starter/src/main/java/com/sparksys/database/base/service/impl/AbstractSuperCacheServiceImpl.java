package com.sparksys.database.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.sparksys.core.utils.KeyUtils;
import com.sparksys.cache.template.CacheTemplate;
import com.sparksys.database.entity.SuperEntity;
import com.sparksys.database.base.mapper.SuperMapper;
import com.sparksys.database.base.service.SuperCacheService;
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
    protected CacheTemplate cacheTemplate;

    /**
     * 缓存key模板
     *
     * @return String
     */
    protected abstract String getRegion();

    @Override
    public T getByIdCache(Serializable id) {
        return this.cacheTemplate.get(this.getRegion(), (x) -> super.getById(id));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        this.cacheTemplate.remove(KeyUtils.buildKey(this.getRegion(), id));
        return bool;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        } else {
            boolean flag = super.removeByIds(idList);
            idList.forEach(id -> this.cacheTemplate.remove(KeyUtils.buildKey(this.getRegion(), id)));
            return flag;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean save(T model) {
        boolean result = super.save(model);
        if (model instanceof SuperEntity) {
            this.cacheTemplate.set(KeyUtils.buildKey(this.getRegion(), ((SuperEntity) model).getId()), model);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        if (model instanceof SuperEntity) {
            this.cacheTemplate.remove(KeyUtils.buildKey(this.getRegion(), ((SuperEntity) model).getId()));
        }
        return updateBool;
    }
}
