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
        boolean idExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.ID);
        if (idExistClass) {
            Object idVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.ID);
            if (ObjectUtils.isEmpty(idVal)) {
                Long id = snowflake.nextId();
                Class<?> idClass = metaObject.getGetterType(EntityConstant.ID);
                idVal = String.class.getName().equals(idClass.getTypeName()) ? String.valueOf(id) : snowflake.nextId();
                this.setFieldValByName(EntityConstant.ID, idVal, metaObject);
            }
        }

        // 创建人
        boolean createUserExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.CREATE_USER);
        if (createUserExistClass) {
            Object createUserVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_USER);
            if (ObjectUtils.isEmpty(createUserVal) || createUserVal.equals(0)) {
                Class<?> createUserClass = metaObject.getGetterType(EntityConstant.CREATE_USER);
                createUserVal = BaseContextHandler.getUserId(createUserClass);
                this.setFieldValByName(EntityConstant.CREATE_USER, createUserVal, metaObject);
            }
        }

        boolean createUserIdExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.CREATE_USER_ID);
        if (createUserIdExistClass) {
            Object createUserIdVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_USER_ID);
            if (ObjectUtils.isEmpty(createUserIdVal) || createUserIdVal.equals(0)) {
                Class<?> createUserIdClass = metaObject.getGetterType(EntityConstant.CREATE_USER_ID);
                createUserIdVal = BaseContextHandler.getUserId(createUserIdClass);
                this.setFieldValByName(EntityConstant.CREATE_USER_ID, createUserIdVal, metaObject);
            }
        }

        boolean createUserNameExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.CREATE_USER_NAME);
        if (createUserNameExistClass) {
            Object createUserNameVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_USER_NAME);
            if (ObjectUtils.isEmpty(createUserNameVal)) {
                createUserNameVal = BaseContextHandler.getName();
                this.setFieldValByName(EntityConstant.CREATE_USER_NAME, createUserNameVal, metaObject);
            }
        }

        // 创建时间
        boolean createTimeExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.CREATE_TIME);
        if (createTimeExistClass) {
            Object createTimeVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.CREATE_TIME);
            if (ObjectUtils.isEmpty(createTimeVal)) {
                Class<?> createTimeClass = metaObject.getGetterType(EntityConstant.CREATE_TIME);
                createTimeVal = Date.class.equals(createTimeClass) ? new Date() : LocalDateTime.now();
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
        boolean updateUserExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.UPDATE_USER);
        if (ObjectUtils.isNotEmpty(updateUserExistClass)) {
            Object updateUserVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_USER);
            if (ObjectUtils.isEmpty(updateUserVal) || updateUserVal.equals(0)) {
                Class<?> updateUserClass = metaObject.getGetterType(EntityConstant.UPDATE_USER);
                updateUserVal = BaseContextHandler.getUserId(updateUserClass);
                this.setFieldValByName(EntityConstant.UPDATE_USER, updateUserVal, metaObject);
            }
        }

        boolean updateUserIdExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.UPDATE_USER_Id);
        if (updateUserIdExistClass) {
            Object updateUserIdVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_USER_Id);
            if (ObjectUtils.isEmpty(updateUserIdVal) || updateUserIdVal.equals(0)) {
                Class<?> updateUserIdClass = metaObject.getGetterType(EntityConstant.UPDATE_USER_Id);
                updateUserIdVal = BaseContextHandler.getUserId(updateUserIdClass);
                this.setFieldValByName(EntityConstant.UPDATE_USER_Id, updateUserIdVal, metaObject);
            }
        }

        boolean updateUserNameExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.UPDATE_USER_NAME);
        if (updateUserNameExistClass) {
            Object updateUserNameVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_USER_NAME);
            if (ObjectUtils.isEmpty(updateUserNameVal)) {
                updateUserNameVal = BaseContextHandler.getName();
                this.setFieldValByName(EntityConstant.UPDATE_USER_NAME, updateUserNameVal, metaObject);
            }
        }

        //更新时间
        boolean updateTimeExistClass = ReflectObjectUtils.existProperty(targetObject, EntityConstant.UPDATE_TIME);
        Object updateTimeVal = ReflectObjectUtils.getValueByKey(targetObject, EntityConstant.UPDATE_TIME);
        if (updateTimeExistClass) {
            if (ObjectUtils.isEmpty(updateTimeVal)) {
                Class<?> updateTimeClass = metaObject.getGetterType(EntityConstant.UPDATE_TIME);
                updateTimeVal = Date.class.equals(updateTimeClass) ? new Date() : LocalDateTime.now();
                this.setFieldValByName(EntityConstant.UPDATE_TIME, updateTimeVal, metaObject);
            }
        }
    }
}
