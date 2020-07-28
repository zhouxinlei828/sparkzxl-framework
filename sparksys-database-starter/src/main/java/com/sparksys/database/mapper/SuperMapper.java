package com.sparksys.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description: 公共mapper
 *
 * @author: zhouxinlei
 * @date: 2020-07-07 19:25:43
 */
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * 查询链
     *
     * @return QueryChainWrapper<T>
     */
    default QueryChainWrapper<T> queryChain() {
        return new QueryChainWrapper<>(this);
    }

    /**
     * lambda查询链
     *
     * @return LambdaQueryChainWrapper<T>
     */
    default LambdaQueryChainWrapper<T> lambdaQueryChain() {
        return new LambdaQueryChainWrapper<>(this);
    }

    /**
     * 更新链
     *
     * @return UpdateChainWrapper<T>
     */
    default UpdateChainWrapper<T> updateChain() {
        return new UpdateChainWrapper<>(this);
    }

    /**
     * lambda更新链
     *
     * @return UpdateChainWrapper<T>
     */
    default LambdaUpdateChainWrapper<T> lambdaUpdateChain() {
        return new LambdaUpdateChainWrapper<>(this);
    }

    /**
     * 批量新增
     *
     * @param entityList 实体列表
     * @return int
     */
    int insertBatchSomeColumn(List<T> entityList);

    /**
     * 更新公共字段
     *
     * @param entity 实体
     * @return int
     */
    int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据主键删除
     *
     * @param entity 实体
     * @return int
     */
    int deleteByIdWithFill(T entity);

    /**
     * 删除全部
     *
     * @return int
     */
    int deleteAll();
}
