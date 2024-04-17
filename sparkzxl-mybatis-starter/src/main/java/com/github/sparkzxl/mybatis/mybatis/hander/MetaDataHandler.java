package com.github.sparkzxl.mybatis.mybatis.hander;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.mybatis.constant.EntityConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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

    private UidGenerator uidGenerator;

    /**
     * 在insert时， createdBy: 自动赋予 当前线程上的登录人id createdAt: 自动赋予 服务器的当前时间
     *
     * @param metaObject 元数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 主键
        injectId(metaObject);
        injectField(metaObject, EntityConstant.CREATED_BY_FIELD, BaseContextConstants.JWT_KEY_USER_ID);
        injectField(metaObject, EntityConstant.CREATED_AT_FIELD, null);
        injectField(metaObject, EntityConstant.TENANT_ID_FIELD, BaseContextConstants.TENANT_ID);
        updateCommonColumn(metaObject);
    }

    /**
     * 在update时， updatedBy: 自动赋予 当前线程上的登录人id updatedAt: 自动赋予 服务器的当前时间
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
        injectField(metaObject, EntityConstant.UPDATED_BY_FIELD, BaseContextConstants.JWT_KEY_USER_ID);
        injectField(metaObject, EntityConstant.UPDATED_AT_FIELD, null);
    }

    /**
     * id生成注入对象
     *
     * @param metaObject 元对象
     */
    private void injectId(MetaObject metaObject) {
        if (uidGenerator == null) {
            // 这里使用SpringUtils的方式"异步"获取对象，防止启动时，报循环注入的错
            uidGenerator = SpringContextUtils.getBean(UidGenerator.class);
        }
        Long id = uidGenerator.getUid();// 这里使用SpringUtils的方式"异步"获取对象，防止启动时，报循环注入的错
        if (metaObject.hasGetter(EntityConstant.ID)) {
            Object idVal = this.getFieldValByName(EntityConstant.ID, metaObject);
            if (ObjectUtils.isEmpty(idVal)) {
                idVal = String.class.getName().equals(metaObject.getGetterType(EntityConstant.ID).getTypeName()) ? String.valueOf(id) : id;
                this.setFieldValByName(EntityConstant.ID, idVal, metaObject);
            }
        } else {
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
            // 得到 主键的值
            Object fieldValue = this.getFieldValByName(keyProperty, metaObject);
            // 判断ID 是否有值，有值就不
            if (ObjectUtils.isNotEmpty(fieldValue)) {
                return;
            }
            Object idVal = keyType.getName().equalsIgnoreCase(StrPool.STRING_TYPE_NAME) ? String.valueOf(id) : id;
            this.setFieldValByName(keyProperty, idVal, metaObject);
        }
    }

    /**
     * 公共字段自动注入
     *
     * @param metaObject 元对象
     * @param field      字段属性
     * @param dataKey    查询key
     */
    protected void injectField(MetaObject metaObject, String field, String dataKey) {
        if (metaObject.hasGetter(field)) {
            Object fieldVal = this.getFieldValByName(field, metaObject);
            if (ObjectUtils.isEmpty(fieldVal)) {
                Object newFieldVal;
                Class<?> fieldClass = metaObject.getGetterType(field);
                if (fieldClass.equals(Date.class)) {
                    newFieldVal = new Date();
                } else if (fieldClass.equals(LocalDateTime.class)) {
                    newFieldVal = LocalDateTime.now();
                } else {
                    newFieldVal = RequestLocalContextHolder.get(dataKey, fieldClass);
                }
                this.setFieldValByName(field, newFieldVal, metaObject);
            }
        }
    }

}
