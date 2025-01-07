package com.github.sparkzxl.mybatis.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.sparkzxl.cache.service.CacheService;
import com.github.sparkzxl.core.entity.cache.CacheKey;
import com.github.sparkzxl.core.entity.cache.CacheKeyBuilder;
import com.github.sparkzxl.mybatis.base.mapper.SuperMapper;
import com.github.sparkzxl.mybatis.base.service.SuperCacheService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * description: 缓存接口父类实现类
 * 默认的key规则： #{CacheKeyBuilder#key()}:id
 * <p>
 * 1，getByIdCache：新增的方法： 先查缓存，在查db
 * 2，removeById：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）后，淘汰缓存
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后，淘汰缓存
 * </p>
 *
 * @param <M>
 * @param <T>
 * @author zhouxinlei
 */
public abstract class SuperCacheServiceImpl<M extends SuperMapper<T>, T> extends SuperServiceImpl<M, T> implements SuperCacheService<T> {

    @Autowired
    protected CacheService cacheService;

    protected static final int MAX_BATCH_KEY_SIZE = 20;

    /**
     * 缓存key 构造器
     *
     * @return 缓存key构造器
     */
    protected abstract CacheKeyBuilder cacheKeyBuilder();

    @Override
    public T getByIdCache(Serializable id) {
        CacheKey cacheKey = cacheKeyBuilder().key(id);
        return cacheService.get(cacheKey, (x) -> super.getById(id));
    }

    @Override
    public T getByKey(CacheKey key, Function<CacheKey, Object> loader) {
        Object id = cacheService.get(key, loader);
        return id == null ? null : getByIdCache(Convert.toLong(id));
    }

    @Override
    public List<T> listByKey(CacheKey key, Function<CacheKey, Set<Object>> loader) {
        Set<Object> memberList = cacheService.sMembers(key);
        List<Long> idList = Convert.toList(Long.class, memberList);
        if (CollectionUtils.isEmpty(idList)) {
            Set<Object> objectSet = loader.apply(key);
            for (Object object : objectSet) {
                cacheService.sAdd(key, object);
                idList.add(Convert.toLong(object));
            }
        }
        return listByIds(idList);
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        if (idList.isEmpty()) {
            return Collections.emptyList();
        }
        return findByIds(idList, missIds -> super.listByIds(missIds.stream().filter(Objects::nonNull).collect(Collectors.toList())));
    }

    @Override
    public List<T> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<T>> loader) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 拼接keys
        List<CacheKey> keys = ids.stream().map(cacheKeyBuilder()::key).collect(Collectors.toList());
        // 切割
        List<List<CacheKey>> partitionKeys = Lists.partition(keys, MAX_BATCH_KEY_SIZE);

        // 用切割后的 partitionKeys 分批去缓存查， 返回的是缓存中存在的数据
        List<T> valueList = partitionKeys.stream().map(ks -> (List<T>) cacheService.find(ks)).flatMap(Collection::stream).collect(Collectors.toList());

        // 所有的key
        List<Serializable> keysList = Lists.newArrayList(ids);
        // 缓存不存在的key
        Set<Serializable> missedKeys = Sets.newLinkedHashSet();

        List<T> allList = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            T v = valueList.get(i);
            Serializable k = keysList.get(i);
            if (v == null) {
                missedKeys.add(k);
            } else {
                allList.add(v);
            }
        }
        // 加载miss 的数据，并设置到缓存
        if (CollUtil.isNotEmpty(missedKeys)) {
            if (loader == null) {
                loader = this::listByIds;
            }
            Collection<T> missList = loader.apply(missedKeys);
            missList.forEach(this::setCache);
            allList.addAll(missList);
        }
        return allList;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        delCache(id);
        return bool;
    }

    @Override
    public boolean removeByIds(Collection<?> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        boolean flag = super.removeByIds(idList);
        delCache(idList);
        return flag;
    }

    @Override
    public boolean deletePhysicalById(Serializable id) {
        boolean deleted = super.deletePhysicalById(id);
        delCache(id);
        return deleted;
    }

    @Override
    public boolean update(Wrapper<T> updateWrapper) {
        List<T> list = list(updateWrapper);
        boolean updated = super.update(updateWrapper);
        for (T t : list) {
            delCache(t);
        }
        return updated;
    }

    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        Object id = getId(entity);
        List<T> list = list(updateWrapper);
        boolean updated = super.update(entity, updateWrapper);
        if (ObjectUtils.isNotEmpty(id)) {
            delCache(cacheKeyBuilder().key(id));
        }
        if (CollectionUtils.isNotEmpty(list)) {
            for (T t : list) {
                delCache(t);
            }
        }
        return updated;
    }

    @Override
    public boolean updateAllById(T model) {
        boolean updateBool = super.updateAllById(model);
        delCache(model);
        return updateBool;
    }

    @Override
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        delCache(model);
        return updateBool;
    }

    @Override
    public boolean remove(Wrapper<T> queryWrapper) {
        List<T> list = list(queryWrapper);
        boolean removed = super.remove(queryWrapper);
        for (T t : list) {
            delCache(t);
        }
        return removed;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        BiPredicate<SqlSession, T> predicate = (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        };
        BiConsumer<SqlSession, T> consumer = (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
            // 清理缓存
            delCache(entity);
        };
        String sqlStatement = SqlHelper.getSqlStatement(this.mapperClass, SqlMethod.INSERT_ONE);
        return SqlHelper.executeBatch(getEntityClass(), log, entityList, batchSize, (sqlSession, entity) -> {
            if (predicate.test(sqlSession, entity)) {
                sqlSession.insert(sqlStatement, entity);
            } else {
                consumer.accept(sqlSession, entity);
            }
        });
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
            // 清理缓存
            delCache(entity);
        });
    }

    @Override
    public void refreshCache() {
        list().forEach(this::setCache);
    }

    @Override
    public void clearCache() {
        list().forEach(this::delCache);
    }

    @Override
    public void delCache(Serializable... ids) {
        delCache(Arrays.asList(ids));
    }

    @Override
    public void delCache(CacheKey key) {
        cacheService.del(key);
    }

    @Override
    public void delCache(Collection<?> idList) {
        CacheKey[] keys = idList.stream().map(id -> cacheKeyBuilder().key(id)).toArray(CacheKey[]::new);
        cacheService.del(keys);
    }

    @Override
    public void delCache(T model) {
        Object id = getId(model);
        if (id != null) {
            CacheKey key = cacheKeyBuilder().key(id);
            cacheService.del(key);
        }
    }

    @Override
    public void setCache(T model) {
        Object id = getId(model);
        if (id != null) {
            CacheKey key = cacheKeyBuilder().key(id);
            cacheService.set(key, model);
        }
    }

    /**
     * 获取主键ID值
     *
     * @param model 对象
     * @return Object
     */
    protected Object getId(T model) {
        if (model == null) {
            return null;
        }
        // 实体没有继承 Entity 和 SuperEntity
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        if (tableInfo == null) {
            return null;
        }
        // 主键类型
        Class<?> keyType = tableInfo.getKeyType();
        if (keyType == null) {
            return null;
        }
        // id 字段名
        String keyProperty = tableInfo.getKeyProperty();
        // 反射得到 主键的值
        Field idField = ReflectUtil.getField(getEntityClass(), keyProperty);
        return ReflectUtil.getFieldValue(model, idField);
    }
}
