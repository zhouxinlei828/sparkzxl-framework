package com.github.sparkzxl.database.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.sparkzxl.cache.service.CacheService;
import com.github.sparkzxl.core.util.KeyGeneratorUtil;
import com.github.sparkzxl.database.base.mapper.SuperMapper;
import com.github.sparkzxl.database.base.service.SuperCacheService;
import com.github.sparkzxl.entity.data.SuperEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

/**
 * description: 缓存接口父类实现类
 *
 * @author zhouxinlei
 */
public abstract class SuperCacheServiceImpl<M extends SuperMapper<T>, T> extends SuperServiceImpl<M, T> implements SuperCacheService<T> {

    protected CacheService cacheService;

    @Autowired
    public void setGeneralCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }


    /**
     * 缓存key模板
     *
     * @return String
     */
    protected abstract String getRegion();

    @Override
    public T getByIdCache(Serializable id) {
        return cacheService.get(KeyGeneratorUtil.generateKey(this.getRegion(), id), (x) -> super.getById(id));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        cacheService.remove(KeyGeneratorUtil.generateKey(this.getRegion(), id));
        return bool;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeByIds(Collection<?> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        } else {
            boolean flag = super.removeByIds(idList);
            idList.forEach(id -> this.cacheService.remove(KeyGeneratorUtil.generateKey(this.getRegion(), id)));
            return flag;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean save(T model) {
        boolean result = super.save(model);
        if (model instanceof SuperEntity) {
            this.cacheService.set(KeyGeneratorUtil.generateKey(this.getRegion(), ((SuperEntity) model).getId()), model);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        if (model instanceof SuperEntity) {
            this.cacheService.remove(KeyGeneratorUtil.generateKey(this.getRegion(), ((SuperEntity) model).getId()));
        }
        return updateBool;
    }
}
