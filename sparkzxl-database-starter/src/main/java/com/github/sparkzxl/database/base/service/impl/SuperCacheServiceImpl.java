package com.github.sparkzxl.database.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.sparkzxl.core.utils.BuildKeyUtils;
import com.github.sparkzxl.cache.template.CacheTemplate;
import com.github.sparkzxl.database.base.mapper.SuperMapper;
import com.github.sparkzxl.database.base.service.SuperCacheService;
import com.github.sparkzxl.database.entity.SuperEntity;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * description: 缓存接口父类实现类
 *
 * @author: zhouxinlei
 * @date: 2020-07-07 19:38:37
 */
public abstract class SuperCacheServiceImpl<M extends SuperMapper<T>, T> extends SuperServiceImpl<M, T> implements SuperCacheService<T> {

    private final long expireTime = 1;

    protected CacheTemplate cacheTemplate;

    @Autowired(required = false)
    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    /**
     * 缓存key模板
     *
     * @return String
     */
    protected abstract String getRegion();

    @Override
    public T getByIdCache(Serializable id) {
        return this.cacheTemplate.get(BuildKeyUtils.generateKey(this.getRegion(), id), (x) -> super.getById(id), expireTime, TimeUnit.DAYS);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        this.cacheTemplate.remove(BuildKeyUtils.generateKey(this.getRegion(), id));
        return bool;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        } else {
            boolean flag = super.removeByIds(idList);
            idList.forEach(id -> this.cacheTemplate.remove(BuildKeyUtils.generateKey(this.getRegion(), id)));
            return flag;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean save(T model) {
        boolean result = super.save(model);
        if (model instanceof SuperEntity) {
            this.cacheTemplate.set(BuildKeyUtils.generateKey(this.getRegion(), ((SuperEntity) model).getId()), model);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        if (model instanceof SuperEntity) {
            this.cacheTemplate.remove(BuildKeyUtils.generateKey(this.getRegion(), ((SuperEntity) model).getId()));
        }
        return updateBool;
    }
}
