package com.sparksys.commons.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * description: 公共mapper
 *
 * @author: zhouxinlei
 * @date: 2020-07-07 19:25:43
 */
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * 以下定义的 4个 default method, copy from {@link com.baomidou.mybatisplus.extension.toolkit.ChainWrappers}
     */
    default QueryChainWrapper<T> queryChain() {
        return new QueryChainWrapper<>(this);
    }

    default LambdaQueryChainWrapper<T> lambdaQueryChain() {
        return new LambdaQueryChainWrapper<>(this);
    }

    default UpdateChainWrapper<T> updateChain() {
        return new UpdateChainWrapper<>(this);
    }

    default LambdaUpdateChainWrapper<T> lambdaUpdateChain() {
        return new LambdaUpdateChainWrapper<>(this);
    }

    /**
     * 以下定义的 4个 method 其中 3 个是内置的选装件
     */
    int insertBatchSomeColumn(List<T> entityList);

    /**
     * 更新公共字段
     *
     * @param entity
     * @return
     */
    int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据主键删除
     *
     * @param entity
     * @return
     */
    int deleteByIdWithFill(T entity);

    /**
     * 删除全部
     *
     * @return int
     */
    int deleteAll();
}
