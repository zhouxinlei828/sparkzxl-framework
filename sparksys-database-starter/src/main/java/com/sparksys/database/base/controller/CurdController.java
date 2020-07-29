package com.sparksys.database.base.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.sparksys.core.utils.DateUtils;
import com.sparksys.core.utils.ListUtils;
import com.sparksys.database.dto.PageParams;
import com.sparksys.database.mybatis.conditions.Wraps;
import com.sparksys.database.mybatis.conditions.query.QueryWrap;
import com.sparksys.database.utils.PageInfoUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description: curd 接口自动生成类
 *
 * @author: zhouxinlei
 * @date: 2020-07-29 09:48:00
 */
public interface CurdController<Entity, Id extends Serializable, SaveDTO, UpdateDTO, PageDTO> extends BaseController<Entity> {

    /**
     * 新增
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增")
    @PostMapping
    default boolean save(@RequestBody @Validated SaveDTO saveDTO) {
        boolean result = handlerSave(saveDTO);
        if (result) {
            Entity model = BeanUtil.toBean(saveDTO, getEntityClass());
            return getBaseService().save(model);
        }
        return false;
    }

    /**
     * 自定义新增
     *
     * @param model 保存DTO
     * @return 返回true, 调用默认更新, 返回其他不调用默认更新
     */
    default boolean handlerSave(SaveDTO model) {
        return true;
    }


    /**
     * 修改
     *
     * @param updateDTO 更新参数
     * @return boolean
     */
    @ApiOperation(value = "修改")
    @PutMapping
    default boolean update(@RequestBody @Validated UpdateDTO updateDTO) {
        boolean result = handlerUpdate(updateDTO);
        if (result) {
            Entity model = BeanUtil.toBean(updateDTO, getEntityClass());
            return getBaseService().updateById(model);
        }
        return false;
    }

    /**
     * 自定义更新
     *
     * @param model 保存DTO
     * @return 返回true, 调用默认更新, 返回其他不调用默认更新
     */
    default boolean handlerUpdate(UpdateDTO model) {
        return true;
    }


    /**
     * 删除方法
     *
     * @param ids ids
     * @return boolean
     */
    @ApiOperation(value = "删除")
    @DeleteMapping
    @ApiImplicitParams({@ApiImplicitParam(name = "ids[]", value = "主键id", dataType = "array", paramType = "query"),
    })
    default boolean delete(@RequestParam("ids[]") List<Id> ids) {
        boolean result = handlerDelete(ids);
        if (result) {
            return getBaseService().removeByIds(ids);
        }
        return false;
    }

    /**
     * 自定义删除
     *
     * @param ids ids
     * @return 返回Strue, 调用默认更新, 返回其他不调用默认更新
     */
    default boolean handlerDelete(List<Id> ids) {
        return true;
    }

    /**
     * 处理参数
     *
     * @param params 分页参数
     */
    default void handlerQueryParams(PageParams<PageDTO> params) {

    }

    /**
     * 执行查询
     * 可以覆盖后重写查询逻辑
     *
     * @param params 分页参数
     * @return 分页结果
     */
    default PageInfo<?> query(PageParams<PageDTO> params) {
        handlerQueryParams(params);
        Entity model = BeanUtil.toBean(params.getModel(), getEntityClass());
        QueryWrap<Entity> wrapper = handlerWrapper(model, params);
        params.buildPage();
        List<Entity> entityList = getBaseService().list(wrapper);
        // 处理结果
        List<?> list = handlerResult(entityList);
        if (ListUtils.isNotEmpty(list)) {
            return PageInfoUtils.pageInfo(list);
        } else if (ListUtils.isNotEmpty(entityList)) {
            return PageInfoUtils.pageInfo(entityList);
        }
        return PageInfoUtils.pageInfo(Lists.newArrayList());
    }

    /**
     * 条件构造
     *
     * @param model  对象
     * @param params 分页参数
     * @return QueryWrap<Entity>
     */
    default QueryWrap<Entity> handlerWrapper(Entity model, PageParams<PageDTO> params) {
        QueryWrap<Entity> wrapper = model == null ? Wraps.q() : Wraps.q(model);
        if (CollUtil.isNotEmpty(params.getMap())) {
            Map<String, String> map = params.getMap();
            //拼装区间
            for (Map.Entry<String, String> field : map.entrySet()) {
                String key = field.getKey();
                String value = field.getValue();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                Date date = DateUtils.parse(value);
                if (key.endsWith("_st")) {
                    String beanField = StrUtil.subBefore(key, "_st", true);
                    wrapper.ge(getDbField(beanField, getEntityClass()), DateUtils.beginOfDay(date));
                }
                if (key.endsWith("_ed")) {
                    String beanField = StrUtil.subBefore(key, "_ed", true);
                    wrapper.le(getDbField(beanField, getEntityClass()), DateUtils.endOfDay(date));
                }
            }
        }
        return wrapper;
    }

    /**
     * 自定义处理返回结果
     *
     * @param entityList 数据返回list
     * @return List<?>
     */
    default List<?> handlerResult(List<Entity> entityList) {
        // 调用注入方法
        return Lists.newArrayList();
    }

    /**
     * 根据 bean字段 反射出 数据库字段
     *
     * @param beanField 实体字段
     * @param clazz     类
     * @return String
     */
    default String getDbField(String beanField, Class<?> clazz) {
        Field field = ReflectUtil.getField(clazz, beanField);
        if (field == null) {
            return StrUtil.EMPTY;
        }
        TableField tf = field.getAnnotation(TableField.class);
        if (tf != null && StringUtils.isNotEmpty(tf.value())) {
            return tf.value();
        }
        return StrUtil.EMPTY;
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键", dataType = "long", paramType = "query")})
    @ApiOperation(value = "查询", notes = "查询")
    @GetMapping("/{id}")
    default Entity get(@PathVariable Id id) {
        return getBaseService().getById(id);
    }

    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return PageInfo<?>
     */
    @ApiOperation(value = "分页列表查询")
    @PostMapping(value = "/page")
    default PageInfo<?> page(@RequestBody @Validated PageParams<PageDTO> params) {
        return query(params);
    }

    /**
     * 批量查询
     *
     * @param data list查询
     * @return 查询结果
     */
    @ApiOperation(value = "批量查询", notes = "批量查询")
    @PostMapping("/list")
    default List<Entity> query(@RequestBody Entity data) {
        QueryWrap<Entity> wrapper = Wraps.q(data);
        return getBaseService().list(wrapper);
    }

}
