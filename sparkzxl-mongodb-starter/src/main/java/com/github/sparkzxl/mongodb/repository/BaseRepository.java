package com.github.sparkzxl.mongodb.repository;

import com.github.sparkzxl.core.context.BaseContextHandler;
import com.github.sparkzxl.core.utils.MapHelper;
import com.github.sparkzxl.mongodb.constant.EntityConstant;
import com.github.sparkzxl.mongodb.entity.SuperEntity;
import com.github.sparkzxl.mongodb.page.MongoPageUtils;
import com.github.sparkzxl.mongodb.page.PageInfo;
import com.github.sparkzxl.mongodb.utils.MongoDbHandleUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BaseRepository<T extends SuperEntity> implements IBaseRepository<T> {

    protected Class<T> entityClass;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Override
    public int insert(T entity) {
        Long userId = BaseContextHandler.getUserId(Long.TYPE);
        entity.setCreateUser(userId);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateUser(userId);
        entity.setUpdateTime(LocalDateTime.now());
        T insert = mongoTemplate.insert(entity);
        return ObjectUtils.isNotEmpty(insert.getId()) ? 1 : 0;
    }


    @Override
    public int insertMulti(Collection<T> entityList) {
        Long userId = BaseContextHandler.getUserId(Long.TYPE);
        for (T entity : entityList) {
            entity.setCreateUser(userId);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateUser(userId);
            entity.setUpdateTime(LocalDateTime.now());
        }
        Collection<T> collection = mongoTemplate.insert(entityList, getEntityClass());
        return collection.size();
    }


    @Override
    public long insertOrUpdate(T entity) {
        if (ObjectUtils.isNotEmpty(entity.getId())) {
            return updateById(entity);
        }
        return insert(entity);
    }

    @Override
    public long removeById(Serializable id) {
        DeleteResult deleteResult = mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), getEntityClass());
        return deleteResult.getDeletedCount();
    }

    @Override
    public long removeByIds(Collection<? extends Serializable> idList) {
        DeleteResult deleteResult = mongoTemplate.remove(new Query(Criteria.where("_id").in(idList)), getEntityClass());
        return deleteResult.getDeletedCount();
    }

    @Override
    public long remove(T entity) {
        DeleteResult deleteResult = mongoTemplate.remove(entity);
        return deleteResult.getDeletedCount();
    }

    @Override
    public long updateById(T entity) {
        Long userId = BaseContextHandler.getUserId(Long.TYPE);
        entity.setUpdateUser(userId);
        entity.setUpdateTime(LocalDateTime.now());
        Map<String, Object> annotationValueMap = MongoDbHandleUtil.getAndAnnotationValue(entity);
        Update update = new Update();
        MapHelper.removeNullValue(annotationValueMap);
        for (String key : annotationValueMap.keySet()) {
            update.set(key, annotationValueMap.get(key));
        }
        UpdateResult updateResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(entity.getId())),
                update, entity.getClass());
        return updateResult.getModifiedCount();
    }

    @Override
    public long update(T entity) {
        entity.setUpdateUser(BaseContextHandler.getUserId(entity.getUpdateUser().getClass()));
        entity.setUpdateTime(LocalDateTime.now());
        Map<String, Object> annotationValueMap = MongoDbHandleUtil.getAndAnnotationValue(entity);
        annotationValueMap.remove(EntityConstant.COLUMN_CREATE_USER);
        annotationValueMap.remove(EntityConstant.COLUMN_CREATE_TIME);
        Update update = new Update();
        for (String key : annotationValueMap.keySet()) {
            update.set(key, annotationValueMap.get(key));
        }
        UpdateResult updateResult = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(entity.getId())), update, getEntityClass());
        return updateResult.getModifiedCount();
    }

    @Override
    public long updateMultiById(Collection<T> entityList) {
        AtomicLong atomicLong = new AtomicLong(0);
        for (T t : entityList) {
            updateById(t);
            atomicLong.getAndIncrement();
        }
        return atomicLong.get();
    }

    @Override
    public T findById(Serializable id) {
        return mongoTemplate.findById(id, getEntityClass());
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, getEntityClass());
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, getEntityClass());
    }

    @Override
    public long count() {
        return mongoTemplate.findAll(getEntityClass()).stream().count();
    }

    @Override
    public List<T> findList(Query query) {
        return mongoTemplate.find(query, getEntityClass());
    }

    @Override
    public List<T> findList() {
        return mongoTemplate.findAll(getEntityClass());
    }

    @Override
    public List<T> findBatchIds(Collection<? extends Serializable> idList) {
        return mongoTemplate.find(new Query(Criteria.where("_id").in(idList)), getEntityClass());
    }

    @Override
    public PageInfo<T> findPage(Query query, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        query.with(pageable);
        long count = mongoTemplate.count(query, getEntityClass());
        List<T> list = mongoTemplate.find(query, getEntityClass());
        return MongoPageUtils.pageInfo(list, count, pageable);
    }

    @Override
    public PageInfo<T> findPage(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Query query = new Query().with(pageable);
        query.with(pageable);
        long count = mongoTemplate.count(query, getEntityClass());
        List<T> list = mongoTemplate.find(query, getEntityClass());
        return MongoPageUtils.pageInfo(list, count, pageable);
    }

    @Override
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
        }
        return this.entityClass;
    }
}
