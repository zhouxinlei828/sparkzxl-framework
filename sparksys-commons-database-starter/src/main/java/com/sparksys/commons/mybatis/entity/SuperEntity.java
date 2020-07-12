package com.sparksys.commons.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-06-17 20:14:48
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class SuperEntity<T> implements Serializable {

    private static final long serialVersionUID = -4603650115461757622L;

    public static final String FIELD_ID = "id";
    public static final String CREATE_TIME = "createTime";
    public static final String CREATE_TIME_COLUMN = "create_time";
    public static final String CREATE_USER = "createUser";
    public static final String CREATE_USER_COLUMN = "create_user";

    @TableId(value = FIELD_ID, type = IdType.INPUT)
    protected T id;
    @TableField(value = CREATE_TIME_COLUMN, fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime createTime;

    @TableField(value = CREATE_USER_COLUMN, fill = FieldFill.INSERT)
    protected T createUser;
}
