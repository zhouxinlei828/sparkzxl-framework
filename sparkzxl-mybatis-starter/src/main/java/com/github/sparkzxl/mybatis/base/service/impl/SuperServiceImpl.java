package com.github.sparkzxl.mybatis.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.mybatis.base.mapper.SuperMapper;
import com.github.sparkzxl.mybatis.base.service.SuperService;

import java.lang.reflect.ParameterizedType;

import static com.github.sparkzxl.core.support.code.ExceptionErrorCode.SERVICE_MAPPER_ERROR;

/**
 * description:
 *
 * @author zhouxinlei
 */
public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {

    private Class<T> entityClass = null;

    public SuperServiceImpl() {
    }

    public SuperMapper getSuperMapper() {
        if (baseMapper instanceof SuperMapper) {
            return baseMapper;
        }
        throw new BizException(SERVICE_MAPPER_ERROR);
    }

    @Override
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        }
        return this.entityClass;
    }

}
