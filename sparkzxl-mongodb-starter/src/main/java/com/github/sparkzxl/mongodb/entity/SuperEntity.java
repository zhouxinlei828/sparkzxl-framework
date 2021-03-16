package com.github.sparkzxl.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * description: base 属性
 *
 * @author zhouxinlei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SuperEntity<E> extends Entity<E> {

    @Field(value = "update_user")
    public String updateUser;

    @Field(value = "update_user_name")
    public String updateUserName;

    @Field(value = "update_time")
    public LocalDateTime updateTime;
}
