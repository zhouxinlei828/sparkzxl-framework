package com.github.sparkzxl.mongodb.service;

import com.github.sparkzxl.mongodb.entity.Entity;
import com.github.sparkzxl.mongodb.repository.IBaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * description: 公共服务类
 *
 * @author zhouxinlei
 * @date 2021-05-15 13:51:54
 */
public interface ISuperService<T extends Entity> {

    boolean save(T entity);

    boolean saveBatch(List<T> entityList);

    boolean saveOrUpdate(T entity);

    boolean saveOrUpdateBatch(Collection<T> entityList);

    boolean removeById(Serializable id);

    boolean removeByIds(Collection<? extends Serializable> idList);

    boolean updateById(T entity);

    boolean update(T entity);

    boolean updateBatchById(Collection<T> entityList);

    T getById(Serializable id);

    List<T> listByIds(Collection<? extends Serializable> idList);

    List<T> list(Query query);

    List<T> list();

    long count();

    long count(Query query);

    Page<T> page(Query query, int pageNum, int pageSize);

    Page<T> page(int pageNum, int pageSize);

    IBaseRepository<T> getBaseRepository();

}
