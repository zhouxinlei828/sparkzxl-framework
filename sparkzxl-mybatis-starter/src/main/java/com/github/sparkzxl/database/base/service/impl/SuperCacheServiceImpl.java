package com.github.sparkzxl.database.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.sparkzxl.cache.service.GeneralCacheService;
import com.github.sparkzxl.core.util.KeyGeneratorUtil;
import com.github.sparkzxl.database.base.mapper.SuperMapper;
import com.github.sparkzxl.database.base.service.SuperCacheService;
import com.github.sparkzxl.entity.data.SuperEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * description: 缓存接口父类实现类
 *
 * @author zhouxinlei
 */
public abstract class SuperCacheServiceImpl<M extends SuperMapper<T>, T> extends SuperServiceImpl<M, T> implements SuperCacheService<T> {

    protected GeneralCacheService generalCacheService;

    @Autowired
    public void setGeneralCacheService(GeneralCacheService generalCacheService) {
        this.generalCacheService = generalCacheService;
    }


    /**
     * 缓存key模板
     *
     * @return String
     */
    protected abstract String getRegion();

    @Override
    public T getByIdCache(Serializable id) {
        long expireTime = 1;
        return this.generalCacheService.get(KeyGeneratorUtil.generateKey(this.getRegion(), id), (x) -> super.getById(id), expireTime, TimeUnit.DAYS);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        this.generalCacheService.remove(KeyGeneratorUtil.generateKey(this.getRegion(), id));
        return bool;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean removeByIds(Collection<?> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        } else {
            boolean flag = super.removeByIds(idList);
            idList.forEach(id -> this.generalCacheService.remove(KeyGeneratorUtil.generateKey(this.getRegion(), id)));
            return flag;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean save(T model) {
        boolean result = super.save(model);
        if (model instanceof SuperEntity) {
            this.generalCacheService.set(KeyGeneratorUtil.generateKey(this.getRegion(), ((SuperEntity) model).getId()), model);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        if (model instanceof SuperEntity) {
            this.generalCacheService.remove(KeyGeneratorUtil.generateKey(this.getRegion(), ((SuperEntity) model).getId()));
        }
        return updateBool;
    }
}
