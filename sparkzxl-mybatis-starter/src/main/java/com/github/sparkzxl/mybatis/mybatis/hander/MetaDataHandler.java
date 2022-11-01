package com.github.sparkzxl.mybatis.mybatis.hander;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.github.sparkzxl.constant.EntityConstant;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.ReflectionUtil;
import com.github.sparkzxl.core.util.StrPool;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Field;
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

    private UidGenerator uidGenerator;

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
        extractId(metaObject, targetObject);
        // 创建人Id
        extractUserId(metaObject, targetObject, EntityConstant.CREATE_USER);
        extractUserId(metaObject, targetObject, EntityConstant.CREATE_USER_ID);
        // 创建人姓名
        extractUserName(metaObject, targetObject, EntityConstant.CREATE_USER_NAME);
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
        //更新时间
        extractDate(metaObject, targetObject, EntityConstant.UPDATE_TIME);

    }

    /**
     * id生成注入对象
     *
     * @param metaObject   元对象
     * @param targetObject 目标对象
     */
    private void extractId(MetaObject metaObject, Object targetObject) {
        boolean idExistClass = ReflectionUtil.existProperty(targetObject, EntityConstant.ID);
        if (idExistClass) {
            if (uidGenerator == null) {
                // 这里使用SpringUtils的方式"异步"获取对象，防止启动时，报循环注入的错
                uidGenerator = SpringContextUtils.getBean(UidGenerator.class);
            }
            Long id = uidGenerator.getUid();
            Object idVal = ReflectionUtil.getValueByKey(targetObject, EntityConstant.ID);
            if (ObjectUtils.isEmpty(idVal)) {
                idVal = String.class.getName().equals(metaObject.getGetterType(EntityConstant.ID).getTypeName()) ? String.valueOf(id) : id;
                this.setFieldValByName(EntityConstant.ID, idVal, metaObject);
            }
        } else {
            if (uidGenerator == null) {
                // 这里使用SpringUtils的方式"异步"获取对象，防止启动时，报循环注入的错
                uidGenerator = SpringContextUtils.getBean(UidGenerator.class);
            }
            Long id = uidGenerator.getUid();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
            if (tableInfo == null) {
                return;
            }
            // 主键类型
            Class<?> keyType = tableInfo.getKeyType();
            if (keyType == null) {
                return;
            }
            // id 字段名
            String keyProperty = tableInfo.getKeyProperty();
            Object oldId = metaObject.getValue(keyProperty);
            if (oldId != null) {
                return;
            }

            // 反射得到 主键的值
            Field idField = ReflectUtil.getField(metaObject.getOriginalObject().getClass(), keyProperty);
            Object fieldValue = ReflectUtil.getFieldValue(metaObject.getOriginalObject(), idField);
            // 判断ID 是否有值，有值就不
            if (ObjectUtil.isNotEmpty(fieldValue)) {
                return;
            }
            Object idVal = keyType.getName().equalsIgnoreCase(StrPool.STRING_TYPE_NAME) ? String.valueOf(id) : id;
            this.setFieldValByName(keyProperty, idVal, metaObject);
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
        boolean userIdExistClass = ReflectionUtil.existProperty(targetObject, field);
        if (userIdExistClass) {
            Object userIdVal = ReflectionUtil.getValueByKey(targetObject, field);
            if (ObjectUtils.isEmpty(userIdVal) || userIdVal.equals(0)) {
                Class<?> userIdClass = metaObject.getGetterType(field);
                userIdVal = RequestLocalContextHolder.getUserId(userIdClass);
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
        boolean userNameExistClass = ReflectionUtil.existProperty(targetObject, field);
        if (userNameExistClass) {
            Object userNameVal = ReflectionUtil.getValueByKey(targetObject, field);
            if (ObjectUtils.isEmpty(userNameVal)) {
                userNameVal = RequestLocalContextHolder.getName();
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
        boolean dateExistClass = ReflectionUtil.existProperty(targetObject, field);
        if (dateExistClass) {
            Object dateVal = ReflectionUtil.getValueByKey(targetObject, field);
            if (ObjectUtils.isEmpty(dateVal)) {
                Class<?> dateClass = metaObject.getGetterType(field);
                dateVal = Date.class.equals(dateClass) ? new Date() : LocalDateTime.now();
                this.setFieldValByName(field, dateVal, metaObject);
            }
        }
    }

}
