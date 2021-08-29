package com.github.sparkzxl.mongodb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * description: 公共属性
 *
 * @author zhouxinlei
 */
@Data
public class Entity<E> implements Serializable {

    private static final long serialVersionUID = -6141149323457188297L;

    @Id
    private E id;

    @Field(value = "create_user")
    public String createUser;

    @Field(value = "create_user_name")
    public String createUserName;

    @Field(value = "create_time")
    public LocalDateTime createTime;

    @Field(value = "update_user")
    public String updateUser;

    @Field(value = "update_user_name")
    public String updateUserName;

    @Field(value = "update_time")
    public LocalDateTime updateTime;

}
