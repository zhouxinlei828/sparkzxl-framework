package com.sparksys.database.mybatis.hander;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.sparksys.database.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import com.sparksys.database.constant.EntityConstant;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * description: mybatis-plus自动注入处理器
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:22:30
 */
@Slf4j
public class MetaDataHandler implements MetaObjectHandler {

    private final Snowflake snowflake;

    public MetaDataHandler(Snowflake snowflake) {
        this.snowflake = snowflake;
    }


    @Override
    public void insertFill(MetaObject metaObject) {
        insertCommonColumn(metaObject);
        updateCommonColumn(metaObject);
    }


    /**
     * 新增相关字段自动填充
     *
     * @param metaObject
     */
    public void insertCommonColumn(MetaObject metaObject) {
        // 主键
        Class idClass = metaObject.getGetterType(EntityConstant.ID);
        Object idVal = metaObject.getValue(EntityConstant.ID);
        if (ObjectUtils.isNotEmpty(idClass)) {
            if (ObjectUtils.isEmpty(idVal)) {
                Long id = snowflake.nextId();
                idVal = String.class.equals(idClass) ? String.valueOf(id) : snowflake.nextId();
                this.setFieldValByName(EntityConstant.ID, idVal, metaObject);
            }
        }

        // 创建人
        Class createUserClass = metaObject.getGetterType(EntityConstant.CREATE_USER);
        Object createUserVal = metaObject.getValue(EntityConstant.CREATE_USER);
        if (ObjectUtils.isNotEmpty(createUserClass)) {
            if (ObjectUtils.isEmpty(createUserVal) || createUserVal.equals(0)) {
                createUserVal = String.class.equals(createUserClass) ?
                        String.valueOf(BaseContextHandler.getUserId()) : BaseContextHandler.getUserId();
                this.setFieldValByName(EntityConstant.CREATE_USER, createUserVal, metaObject);
            }
        }

        // 创建时间
        Class createTimeClass = metaObject.getGetterType(EntityConstant.CREATE_TIME);
        Object createTimeVal = metaObject.getValue(EntityConstant.CREATE_TIME);
        if (ObjectUtils.isNotEmpty(createTimeClass)) {
            if (ObjectUtils.isEmpty(createTimeVal)) {
                createTimeVal = Date.class.equals(createTimeClass) ?
                        new Date() : LocalDateTime.now();
                this.setFieldValByName(EntityConstant.CREATE_TIME, createTimeVal, metaObject);
            }
        }
    }

    /**
     * 所有的继承了Entity、SuperEntity的实体，在update时，
     * updateUser: 自动赋予 当前线程上的登录人id
     * updateTime: 自动赋予 服务器的当前时间
     *
     * @param metaObject 元数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        updateCommonColumn(metaObject);
    }

    /**
     * 更新相关字段自动填充
     *
     * @param metaObject
     */
    public void updateCommonColumn(MetaObject metaObject) {

        //更新人
        Class updateUserClass = metaObject.getGetterType(EntityConstant.UPDATE_USER);
        Object updateUserVal = metaObject.getValue(EntityConstant.UPDATE_USER);
        if (ObjectUtils.isNotEmpty(updateUserClass)) {
            if (ObjectUtils.isEmpty(updateUserVal) || updateUserVal.equals(0)) {
                updateUserVal = String.class.equals(updateUserClass) ?
                        String.valueOf(BaseContextHandler.getUserId()) : BaseContextHandler.getUserId();
                this.setFieldValByName(EntityConstant.UPDATE_USER, updateUserVal, metaObject);
            }
        }

        //更新时间
        Class updateTimeClass = metaObject.getGetterType(EntityConstant.UPDATE_TIME);
        Object updateTimeVal = metaObject.getValue(EntityConstant.UPDATE_TIME);
        if (ObjectUtils.isNotEmpty(updateTimeClass)) {
            if (ObjectUtils.isEmpty(updateTimeVal)) {
                updateTimeVal = Date.class.equals(updateTimeClass) ?
                        new Date() : LocalDateTime.now();
                this.setFieldValByName(EntityConstant.UPDATE_TIME, updateTimeVal, metaObject);
            }
        }
    }
}
