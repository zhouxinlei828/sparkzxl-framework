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
 * description:
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
    boolean updateAllById(T entity);

    /**
     * 物理删除
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     * @return int
     */
    boolean deletePhysical(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据ID物理删除
     *
     * @param id 主键ID
     * @return int
     */
    boolean deletePhysicalById(Serializable id);

}
