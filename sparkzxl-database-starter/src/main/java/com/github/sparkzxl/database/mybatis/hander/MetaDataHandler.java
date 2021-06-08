package com.github.sparkzxl.database.mybatis.hander;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.github.sparkzxl.core.context.BaseContextHolder;
import com.github.sparkzxl.core.utils.ReflectObjectUtils;
import com.github.sparkzxl.database.constant.EntityConstant;
import com.github.sparkzxl.database.enums.IdTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * description: mybatis-plus自动注入处理器
 *
 * @author zhouxinlei
 */
@Slf4j
@Getter
@Setter
public class MetaDataHandler implements MetaObjectHandler {

    private IdTypeEnum idType;

    private Snowflake snowflake;

    @Override
    public void insertFill(MetaObject metaObject) {
        insertCommonColumn(metaObject);
        updateCommonColumn(metaObject);
    }


    /**
     * 所有的继承了Entity、SuperEntity的实体，在insert时，
     * createUser: 自动赋予 当前线程上的登录人id
     * createTime: 自动赋予 服务器的当前时间
     *
     * @param metaObject 元数据
     */
    public void insertCommonColumn(MetaObject metaObject) {
        log.debug("start update fill ....");
        Object targetObject = metaObject.getOriginalObject();
        // 主键
        extractId(metaObject, targetObject, idType, EntityConstant.ID);
        // 创建人Id
        extractUserId(metaObject, targetObject, EntityConstant.CREATE_USER);
        extractUserId(metaObject, targetObject, EntityConstant.CREATE_USER_ID);
        // 创建人姓名
        extractUserName(metaObject, targetObject, EntityConstant.COLUMN_CREATE_USER_NAME);
        // 创建时间
        extractDate(metaObject, targetObject, EntityConstant.CREATE_TIME);

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
     * @param metaObject 元对象
     */
    public void updateCommonColumn(MetaObject metaObject) {
        Object targetObject = metaObject.getOriginalObject();
        //更新人id
        extractUserId(metaObject, targetObject, EntityConstant.UPDATE_USER);
        extractUserId(metaObject, targetObject, EntityConstant.UPDATE_USER_ID);
        extractUserName(metaObject, targetObject, EntityConstant.UPDATE_USER_NAME);
        //更新时间
        extractDate(metaObject, targetObject, EntityConstant.UPDATE_TIME);

    }

    /**
     * id生成注入对象
     *
     * @param metaObject   元对象
     * @param targetObject 目标对象
     * @param idType       生成类型
     * @param field        用户id属性
     */
    private void extractId(MetaObject metaObject, Object targetObject, IdTypeEnum idType, String field) {
        boolean idExistClass = ReflectObjectUtils.existProperty(targetObject, field);
        if (idExistClass) {
            Object idVal = ReflectObjectUtils.getValueByKey(targetObject, field);
            if (ObjectUtils.isEmpty(idVal)) {
                if (idType.equals(IdTypeEnum.RANDOM_UUID)) {
                    idVal = IdUtil.randomUUID();
                } else if (idType.equals(IdTypeEnum.SIMPLE_UUID)) {
                    idVal = IdUtil.simpleUUID();
                } else if (idType.equals(IdTypeEnum.OBJECT_Id)) {
                    idVal = IdUtil.objectId();
                } else if (idType.equals(IdTypeEnum.SNOWFLAKE_ID)) {
                    Long id = snowflake.nextId();
                    Class<?> idClass = metaObject.getGetterType(field);
                    idVal = String.class.getName().equals(idClass.getTypeName()) ? String.valueOf(id) : snowflake.nextId();
                }
                this.setFieldValByName(field, idVal, metaObject);
            }
        }
    }


    /**
     * 全局用户id注入对象
     *
     * @param metaObject   元对象
     * @param targetObject 目标对象
     * @param field        字段属性
     */
    private void extractUserId(MetaObject metaObject, Object targetObject, String field) {
        boolean userIdExistClass = ReflectObjectUtils.existProperty(targetObject, field);
        if (userIdExistClass) {
            Object userIdVal = ReflectObjectUtils.getValueByKey(targetObject, field);
            if (ObjectUtils.isEmpty(userIdVal) || userIdVal.equals(0)) {
                Class<?> userIdClass = metaObject.getGetterType(field);
                userIdVal = BaseContextHolder.getUserId(userIdClass);
                this.setFieldValByName(field, userIdVal, metaObject);
            }
        }
    }

    /**
     * 当前登录用户姓名自动注入
     *
     * @param metaObject   元对象
     * @param targetObject 目标对象
     * @param field        字段属性
     */
    private void extractUserName(MetaObject metaObject, Object targetObject, String field) {
        boolean userNameExistClass = ReflectObjectUtils.existProperty(targetObject, field);
        if (userNameExistClass) {
            Object userNameVal = ReflectObjectUtils.getValueByKey(targetObject, field);
            if (ObjectUtils.isEmpty(userNameVal)) {
                userNameVal = BaseContextHolder.getName();
                this.setFieldValByName(field, userNameVal, metaObject);
            }
        }
    }

    /**
     * 时间扩展自动注入
     *
     * @param metaObject   元对象
     * @param targetObject 目标对象
     * @param field        字段属性
     */
    private void extractDate(MetaObject metaObject, Object targetObject, String field) {
        boolean dateExistClass = ReflectObjectUtils.existProperty(targetObject, field);
        if (dateExistClass) {
            Object dateVal = ReflectObjectUtils.getValueByKey(targetObject, field);
            if (ObjectUtils.isEmpty(dateVal)) {
                Class<?> dateClass = metaObject.getGetterType(field);
                dateVal = Date.class.equals(dateClass) ? new Date() : LocalDateTime.now();
                this.setFieldValByName(field, dateVal, metaObject);
            }
        }
    }

}
