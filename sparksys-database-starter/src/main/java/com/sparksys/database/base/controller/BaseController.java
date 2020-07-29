package com.sparksys.database.base.controller;

import com.sparksys.database.base.service.SuperService;

/**
 * description: 基础controller
 *
 * @author: zhouxinlei
 * @date: 2020-07-29 09:38:01
 */
public interface BaseController<Entity> {

    /**
     * 获取实体的类型
     *
     * @return Class<Entity>
     */
    Class<Entity> getEntityClass();

    /**
     * 获取Service
     *
     * @return SuperService<Entity>
     */
    SuperService<Entity> getBaseService();

}
