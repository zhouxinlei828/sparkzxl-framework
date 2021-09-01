package com.github.sparkzxl.mongodb.event;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.core.context.AppContextHolder;
import com.github.sparkzxl.mongodb.entity.Entity;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * description: mongodb 插入时间监听
 *
 * @author zhouxinlei
 * @date 2021-08-28 19:23:46
 */
public class MongoInsertEventListener extends AbstractMongoEventListener<Entity> {

    private final Snowflake snowflake = IdUtil.getSnowflake(0, 10);

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Entity> event) {
        Entity entity = event.getSource();
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.of("+8"));
        // 判断 id 为空
        if (entity.getId() == null) {
            Number id = snowflake.nextId();
            // noinspection unchecked
            entity.setId(id);
            entity.setCreateTime(dateTime);
            entity.setCreateUser(AppContextHolder.getUserId(String.class));
            entity.setCreateUserName(AppContextHolder.getName());
            entity.setUpdateTime(dateTime);
            entity.setUpdateUser(AppContextHolder.getUserId(String.class));
            entity.setUpdateUserName(AppContextHolder.getName());
        } else {
            entity.setUpdateTime(dateTime);
            entity.setUpdateUser(AppContextHolder.getUserId(String.class));
            entity.setUpdateUserName(AppContextHolder.getName());
        }
    }

}
