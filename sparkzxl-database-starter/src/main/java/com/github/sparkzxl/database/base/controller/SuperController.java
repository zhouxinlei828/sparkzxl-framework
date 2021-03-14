package com.github.sparkzxl.database.base.controller;

import com.github.sparkzxl.database.base.service.SuperService;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * description: SuperController
 *
 * <p>
 * 继承该类，就拥有了如下方法：
 * 1，page 分页查询，并支持子类扩展4个方法：handlerQueryParams、query、handlerWrapper、handlerResult
 * 2，save 保存，并支持子类扩展方法：handlerSave
 * 3，update 修改，并支持子类扩展方法：handlerUpdate
 * 4，delete 删除，并支持子类扩展方法：handlerDelete
 * 5，get 单体查询， 根据ID直接查询DB
 * 6，list 列表查询，根据参数条件，查询列表
 * 7，import 导入，并支持子类扩展方法：handlerImport
 * 8，export 导出，并支持子类扩展3个方法：handlerQueryParams、query、handlerResult
 * 9，preview 导出预览，并支持子类扩展3个方法：handlerQueryParams、query、handlerResult
 * <p>
 * 其中 page、export、preview 的查询条件一致，若子类重写了 handlerQueryParams、query、handlerResult 等任意方法，均衡收到影响
 * <p>
 * 若重写扩展方法无法满足，则可以重写page、save等方法，但切记不要修改 @RequestMapping 参数
 *
 * <S>         Service
 * <Id>        主键
 * <Entity>    实体
 * <PageDTO>   分页参数
 * <SaveDTO>   保存参数
 * <UpdateDTO> 修改参数
 *
 * @author: zhouxinlei
 * @date: 2020-07-29 10:49:32
 */
public abstract class SuperController<S extends SuperService<Entity>, Id extends Serializable, Entity, SaveDTO, UpdateDTO, QueryDTO, ExcelEntity> extends SuperSimpleController<S, Entity>
        implements CurdController<Entity, Id, SaveDTO, UpdateDTO, QueryDTO, ExcelEntity> {


    @Override
    public Class<Entity> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
        }
        return this.entityClass;
    }
}
