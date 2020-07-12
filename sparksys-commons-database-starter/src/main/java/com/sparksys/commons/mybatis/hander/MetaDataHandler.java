package com.sparksys.commons.mybatis.hander;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sparksys.commons.mybatis.entity.Entity;
import com.sparksys.commons.mybatis.entity.SuperEntity;
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

    private final long workerId;

    private final long dataCenterId;

    public MetaDataHandler(long workerId, long dataCenterId) {
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
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
        }

        if (metaObject.getOriginalObject() instanceof Entity) {
            Entity entity = (Entity) metaObject.getOriginalObject();
            this.update(metaObject, entity);
        }
        if (flag) {
            Snowflake snowflake = IdUtil.createSnowflake(this.workerId, this.dataCenterId);
            Long id = snowflake.nextId();
            if (metaObject.hasGetter(SuperEntity.FIELD_ID)) {
                idVal = "java.lang.String".equals(metaObject.getGetterType(SuperEntity.FIELD_ID).getName()) ? String.valueOf(id) : id;
                this.setFieldValByName("id", idVal, metaObject);
            }
        }
    }

    private void update(MetaObject metaObject, Entity entity) {
        if (entity.getUpdateTime() == null) {
            this.setFieldValByName(Entity.UPDATE_TIME, LocalDateTime.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        if (metaObject.getOriginalObject() instanceof Entity) {
            Entity entity = (Entity) metaObject.getOriginalObject();
            this.update(metaObject, entity);
        }
    }

}
