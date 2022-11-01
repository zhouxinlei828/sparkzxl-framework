package com.github.sparkzxl.mybatis.echo.core;


import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * description: 加载数据
 *
 * @author zhouxinlei
 */
public interface LoadService {

    /**
     * 根据id查询实体
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return Map<Serializable, Object>
     */
    Map<Serializable, Object> findByIds(Set<Serializable> ids);


}
