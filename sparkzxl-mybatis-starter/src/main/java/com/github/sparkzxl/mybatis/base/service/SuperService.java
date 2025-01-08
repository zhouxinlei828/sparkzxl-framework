package com.github.sparkzxl.mybatis.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.support.code.ExceptionErrorCode;
import com.github.sparkzxl.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * description: 基于MP的 IService 新增了4个方法： saveBatchSomeColumn、updateAllById、deletePhysical、deletePhysicalById
 * 1，saveBatchSomeColumn 批量插入
 * 2，updateAllById 执行后，会清除缓存
 * 2，deletePhysical 执行后，会物理删除
 * 2，deletePhysicalById 执行后，会物理删除
 *
 * @author zhouxinlei
 */
public interface SuperService<T> extends IService<T> {

    /**
     * 批量保存
     *
     * @param entityList 实体对象
     * @return boolean
     */
    default boolean saveBatchSomeColumn(List<T> entityList) {
        int size = 5000;
        if (entityList.isEmpty()) {
            return true;
        } else if (entityList.size() > size) {
            ExceptionAssert.failure(ExceptionErrorCode.TOO_MUCH_DATA_ERROR);
        } else {
            return SqlHelper.retBool(((SuperMapper<T>) this.getBaseMapper()).insertBatchSomeColumn(entityList));
        }
        return false;
    }

    /**
     * 根据id修改 entity 的所有字段
     *
     * @param entity 实体对象
     * @return boolean
     */
    default boolean updateAllById(T entity) {
        return SqlHelper.retBool(((SuperMapper<T>) this.getBaseMapper()).updateAllById(entity));
    }

    /**
     * 物理删除
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     * @return boolean
     */
    default boolean deletePhysical(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        return SqlHelper.retBool(((SuperMapper<T>) this.getBaseMapper()).deletePhysical(queryWrapper));
    }

    /**
     * 根据ID物理删除
     *
     * @param id 主键ID
     * @return int
     */
    default boolean deletePhysicalById(Serializable id) {
        return SqlHelper.retBool(((SuperMapper<T>) this.getBaseMapper()).deletePhysicalById(id));
    }

}
