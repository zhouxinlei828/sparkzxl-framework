package com.github.sparkzxl.mybatis.base.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * description: 公共mapper
 *
 * @author zhouxinlei
 */
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * 全量修改所有字段
     *
     * @param entity 实体
     * @return 修改数量
     */
    int updateAllById(@Param(Constants.ENTITY) T entity);

    /**
     * 批量插入所有字段
     * <p>
     * 只测试过MySQL！只测试过MySQL！只测试过MySQL！
     *
     * @param entityList 实体集合
     * @return 插入数量
     */
    int insertBatchSomeColumn(List<T> entityList);

    /**
     * 物理删除
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     * @return int
     */
    int deletePhysical(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据ID物理删除
     *
     * @param id 主键ID
     * @return int
     */
    int deletePhysicalById(Serializable id);
}
