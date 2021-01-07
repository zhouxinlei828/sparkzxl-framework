package com.github.sparkzxl.mongodb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
public class Entity<E> {

    @Id
    private E id;

    @Field(value = "create_user")
    public Long createUser;

    @Field(value = "create_user_name")
    public String createUserName;

    @Field(value = "create_time")
    public LocalDateTime createTime;
}
