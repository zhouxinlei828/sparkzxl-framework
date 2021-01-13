package com.github.sparkzxl.mongodb.repository;

import com.github.sparkzxl.mongodb.entity.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * description: 公共仓储层
 *
 * @author: zhouxinlei
 * @date: 2021-01-06 17:59:49
 */
public interface IBaseRepository<T extends Entity> {

    int insert(T entity);

    int insertMulti(Collection<T> entityList);

    long insertOrUpdate(T entity);

    long removeById(Serializable id);

    long removeByIds(Collection<? extends Serializable> idList);

    long remove(T entity);

    long updateById(T entity);

    long update(T entity);

    long updateMultiById(Collection<T> entityList);

    T findById(Serializable id);

    T findOne(Query query);

    long count(Query query);

    long count();

    List<T> findList(Query query);

    List<T> findList();

    List<T> findBatchIds(Collection<? extends Serializable> idList);

    Page<T> findPage(Query query, int pageNum, int pageSize);

    Page<T> findPage(int pageNum, int pageSize);

    Class<T> getEntityClass();
}
