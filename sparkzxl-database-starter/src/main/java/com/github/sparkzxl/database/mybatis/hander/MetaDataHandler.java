package com.github.sparkzxl.database.mybatis.hander;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.github.sparkzxl.core.context.BaseContextHandler;
import com.github.sparkzxl.core.utils.ReflectObjectUtils;
import com.github.sparkzxl.database.constant.EntityConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * description: mybatis-plus自动注入处理器
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:22:30
 */
@Slf4j
@AllArgsConstructor
public class MetaDataHandler implements MetaObjectHandler {

    private final Snowflake snowflake;

    @Override
    public void insertFill(MetaObject metaObject) {
        insertCommonColumn(metaObject);
        updateCommonColumn(metaObject);
    }


    /**
     * 新增相关字段自动填充
     *
     * @param metaObject 元数据
     */
    public void insertCommonColumn(MetaObject metaObject) {
        Object targetObject = metaObject.getOriginalObject();
        // 主键
        Type idClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.ID);
        Object idVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.ID);
        if (ObjectUtils.isNotEmpty(idClass)) {
            if (ObjectUtils.isEmpty(idVal)) {
                Long id = snowflake.nextId();
                idVal = String.class.getName().equals(idClass.getTypeName()) ? String.valueOf(id) : snowflake.nextId();
                this.setFieldValByName(EntityConstant.ID, idVal, metaObject);
            }
        }

        // 创建人
        Type createUserClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.CREATE_USER);
        Object createUserVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_USER);
        if (ObjectUtils.isNotEmpty(createUserClass)) {
            if (ObjectUtils.isEmpty(createUserVal) || createUserVal.equals(0)) {
                createUserVal = BaseContextHandler.getUserId(createUserClass.getClass());
                this.setFieldValByName(EntityConstant.CREATE_USER, createUserVal, metaObject);
            }
        }

        Type createUserIdClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.CREATE_USER_ID);
        Object createUserIdVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_USER_ID);
        if (ObjectUtils.isNotEmpty(createUserIdClass)) {
            if (ObjectUtils.isEmpty(createUserIdVal) || createUserIdVal.equals(0)) {
                createUserIdVal = BaseContextHandler.getUserId(createUserIdClass.getClass());
                this.setFieldValByName(EntityConstant.CREATE_USER_ID, createUserIdVal, metaObject);
            }
        }

        Type createUserNameClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.CREATE_USER_NAME);
        Object createUserNameVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_USER_NAME);
        if (ObjectUtils.isNotEmpty(createUserNameClass)) {
            if (ObjectUtils.isEmpty(createUserNameVal)) {
                createUserNameVal = BaseContextHandler.getName();
                this.setFieldValByName(EntityConstant.CREATE_USER_NAME, createUserNameVal, metaObject);
            }
        }

        // 创建时间
        Type createTimeClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.CREATE_TIME);
        Object createTimeVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_TIME);
        if (ObjectUtils.isNotEmpty(createTimeClass)) {
            if (ObjectUtils.isEmpty(createTimeVal)) {
                createTimeVal = Date.class.getName().equals(createTimeClass.getTypeName()) ?
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
        Object targetObject = metaObject.getOriginalObject();
        //更新人
        Type updateUserClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.UPDATE_USER);
        Object updateUserVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_USER);
        if (ObjectUtils.isNotEmpty(updateUserClass)) {
            if (ObjectUtils.isEmpty(updateUserVal) || updateUserVal.equals(0)) {
                updateUserVal = BaseContextHandler.getUserId(updateUserClass.getClass());
                this.setFieldValByName(EntityConstant.UPDATE_USER, updateUserVal, metaObject);
            }
        }

        Type updateUserIdClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.UPDATE_USER_Id);
        Object updateUserIdVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_USER_Id);
        if (ObjectUtils.isNotEmpty(updateUserIdClass)) {
            if (ObjectUtils.isEmpty(updateUserIdVal) || updateUserIdVal.equals(0)) {
                updateUserIdVal = BaseContextHandler.getUserId(updateUserIdClass.getClass());
                this.setFieldValByName(EntityConstant.UPDATE_USER_Id, updateUserIdVal, metaObject);
            }
        }

        Type updateUserNameClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.UPDATE_USER_NAME);
        Object updateUserNameVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_USER_NAME);
        if (ObjectUtils.isNotEmpty(updateUserNameClass)) {
            if (ObjectUtils.isEmpty(updateUserNameVal)) {
                updateUserNameVal = BaseContextHandler.getName();
                this.setFieldValByName(EntityConstant.UPDATE_USER_NAME, updateUserNameVal, metaObject);
            }
        }

        //更新时间
        Type updateTimeClass = ReflectObjectUtils.getPropertyType(targetObject, EntityConstant.UPDATE_TIME);
        Object updateTimeVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_TIME);
        if (ObjectUtils.isNotEmpty(updateTimeClass)) {
            if (ObjectUtils.isEmpty(updateTimeVal)) {
                updateTimeVal = Date.class.getName().equals(updateTimeClass.getTypeName()) ?
                        new Date() : LocalDateTime.now();
                this.setFieldValByName(EntityConstant.UPDATE_TIME, updateTimeVal, metaObject);
            }
        }
    }
}
