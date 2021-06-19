package com.github.sparkzxl.mongodb.repository;

import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.core.utils.MapHelper;
import com.github.sparkzxl.mongodb.constant.EntityConstant;
import com.github.sparkzxl.mongodb.entity.SuperEntity;
import com.github.sparkzxl.mongodb.utils.MongoDbHandleUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/***
 * description: 公共仓储层 实现类
 *
 * @author zhouxinlei
 * @date 2021-05-15 13:46:58
 */
public class BaseRepository<T extends SuperEntity> implements IBaseRepository<T> {

    protected Class<T> entityClass;

    protected MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public int insert(T entity) {
        entity.setBusinessId(IdUtil.objectId());
        entity.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        entity.setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        T insert = mongoTemplate.insert(entity);
        return ObjectUtils.isNotEmpty(insert.getId()) ? 1 : 0;
    }


    @Override
    public int insertMulti(Collection<T> entityList) {
        for (T entity : entityList) {
            entity.setBusinessId(IdUtil.objectId());
            entity.setCreateTime(LocalDateTime.now());
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
    public Page<T> findPage(Query query, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        long count = mongoTemplate.count(query, getEntityClass());
        List<T> list = mongoTemplate.find(query.with(pageable), getEntityClass());
        return PageableExecutionUtils.getPage(list, pageable, () -> count);
    }

    @Override
    public Page<T> findPage(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Query query = new Query().with(pageable);
        long count = mongoTemplate.count(query, getEntityClass());
        List<T> list = mongoTemplate.find(query.with(pageable), getEntityClass());
        return PageableExecutionUtils.getPage(list, pageable, () -> count);
    }

    @Override
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return this.entityClass;
    }
}
