package com.github.sparkzxl.mybatis.base.controller;

import com.github.sparkzxl.mybatis.base.service.SuperCacheService;
import java.io.Serializable;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * description: super 缓存controller
 *
 * @author zhouxinlei
 */
public abstract class SuperCacheController<S extends SuperCacheService<Entity>, Id extends Serializable, Entity, SaveDTO, UpdateDTO, QueryDTO, ExcelEntity>
        extends SuperController<S, Id, Entity, SaveDTO, UpdateDTO, QueryDTO, ExcelEntity> {

    /**
     * 查询
     *
     * @param id 主键id
     * @return 查询结果
     */
    @Override
    public Entity get(@PathVariable Id id) {
        return baseService.getByIdCache(id);
    }

}
