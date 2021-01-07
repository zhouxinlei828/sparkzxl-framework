package com.github.sparkzxl.mongodb.service;

import com.github.sparkzxl.mongodb.entity.Entity;
import com.github.sparkzxl.mongodb.page.PageInfo;
import com.github.sparkzxl.mongodb.repository.IBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class SuperServiceImpl<R extends IBaseRepository<T>, T extends Entity> implements ISuperService<T> {

    @Autowired
    protected R baseRepository;

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public boolean save(T entity) {
        return getBaseRepository().insert(entity) != 0;
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public boolean saveBatch(List<T> entityList) {
        return getBaseRepository().insertMulti(entityList) != 0;
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public boolean saveOrUpdate(T entity) {
        return getBaseRepository().insertOrUpdate(entity) != 0;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        for (T entity : entityList) {
            getBaseRepository().insertOrUpdate(entity);
        }
        return true;
    }


    @Override
    public boolean removeById(Serializable id) {
        return this.getBaseRepository().removeById(id) == 1;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return !CollectionUtils.isEmpty(idList) && this.getBaseRepository().removeByIds(idList) != 0;
    }

    @Override
    public boolean updateById(T entity) {
        return this.getBaseRepository().updateById(entity) != 0;
    }

    @Override
    public boolean update(T entity) {
        return this.getBaseRepository().update(entity) != 0;
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return this.getBaseRepository().updateMultiById(entityList) != 0;
    }


    @Override
    public T getById(Serializable id) {
        return getBaseRepository().findById(id);
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        return this.getBaseRepository().findBatchIds(idList);
    }

    @Override
    public List<T> list(Query query) {
        return this.getBaseRepository().findList(query);
    }

    @Override
    public List<T> list() {
        return this.getBaseRepository().findList();
    }

    @Override
    public long count() {
        return this.getBaseRepository().count();
    }

    @Override
    public long count(Query query) {
        return this.getBaseRepository().count(query);
    }

    @Override
    public PageInfo<T> page(Query query, int pageNum, int pageSize) {
        return this.getBaseRepository().findPage(query, pageNum, pageSize);
    }

    @Override
    public PageInfo<T> page(int pageNum, int pageSize) {
        return this.getBaseRepository().findPage(pageNum, pageSize);
    }

    @Override
    public IBaseRepository<T> getBaseRepository() {
        return baseRepository;
    }

}
