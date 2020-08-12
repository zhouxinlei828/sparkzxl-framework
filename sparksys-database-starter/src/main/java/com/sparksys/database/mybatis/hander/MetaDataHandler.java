package com.sparksys.database.mybatis.hander;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sparksys.database.context.BaseContextHandler;
import com.sparksys.database.entity.Entity;
import com.sparksys.database.entity.SuperEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

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
        boolean flag = true;
        Object idVal;
        if (metaObject.getOriginalObject() instanceof SuperEntity) {
            Object oldId = ((SuperEntity) metaObject.getOriginalObject()).getId();
            if (oldId != null) {
                flag = false;
            }
            SuperEntity entity = (SuperEntity) metaObject.getOriginalObject();
            if (entity.getCreateTime() == null) {
                this.setFieldValByName(SuperEntity.CREATE_TIME, LocalDateTime.now(), metaObject);
            }
            if (entity.getCreateUser() == null || entity.getCreateUser().equals(0)) {
                Object userIdVal = String.class.getName().equals(metaObject.getGetterType(SuperEntity.CREATE_USER).getName()) ?
                        String.valueOf(BaseContextHandler.getUserId()) : BaseContextHandler.getUserId();
                this.setFieldValByName(Entity.CREATE_USER, userIdVal, metaObject);
            }
        }

        if (metaObject.getOriginalObject() instanceof Entity) {
            Entity entity = (Entity) metaObject.getOriginalObject();
            this.update(metaObject, entity);
        }
        if (flag) {
            Long id = snowflake.nextId();
            idVal = "java.lang.String".equals(metaObject.getGetterType(SuperEntity.FIELD_ID).getName()) ? String.valueOf(id) : id;
            this.setFieldValByName("id", idVal, metaObject);
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
        if (metaObject.getOriginalObject() instanceof Entity) {
            Entity entity = (Entity) metaObject.getOriginalObject();
            this.update(metaObject, entity);
        } else {
            Object et = metaObject.getValue(Constants.ENTITY);
            if (et != null && et instanceof Entity) {
                Entity entity = (Entity) et;
                update(metaObject, entity, Constants.ENTITY + ".");
            }
        }
    }

    private void update(MetaObject metaObject, Entity entity, String et) {
        if (entity.getUpdateUser() == null || entity.getUpdateUser().equals(0)) {
            Object userIdVal = String.class.getName().equals(metaObject.getGetterType(et + Entity.UPDATE_USER).getName()) ?
                    String.valueOf(BaseContextHandler.getUserId()) : BaseContextHandler.getUserId();
            this.setFieldValByName(Entity.UPDATE_USER, userIdVal, metaObject);
        }
        if (entity.getUpdateTime() == null) {
            this.setFieldValByName(Entity.UPDATE_TIME, LocalDateTime.now(), metaObject);
        }
    }

    private void update(MetaObject metaObject, Entity entity) {
        update(metaObject, entity, "");
    }

}
