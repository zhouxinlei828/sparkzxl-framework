package com.github.sparkzxl.mongodb.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * description: 公共属性
 *
 * @author zhouxinlei
 */
@Getter
@Setter
public class Entity<E> implements Serializable {


    private static final long serialVersionUID = 1932221777234584892L;
    @CreatedBy
    public String createdBy;
    @Field(value = "create_user_name")
    public String createUserName;
    @CreatedDate
    public LocalDateTime createdTime;
    @LastModifiedBy
    public String updatedBy;
    @Field(value = "update_user_name")
    public String updateUserName;
    @LastModifiedDate
    public LocalDateTime updatedTime;
    @Id
    @Indexed(unique = true)
    private E id;

}
