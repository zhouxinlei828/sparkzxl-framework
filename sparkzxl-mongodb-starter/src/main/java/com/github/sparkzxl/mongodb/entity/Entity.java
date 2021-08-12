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

    private static final long serialVersionUID = -7738124684295897274L;
    @Field(value = "create_user")
    public String createUser;
    @Field(value = "create_user_name")
    public String createUserName;
    @Field(value = "create_time")
    public LocalDateTime createTime;
    @Id
    private E id;
    @Field(value = "business_id")
    private String businessId;
}
