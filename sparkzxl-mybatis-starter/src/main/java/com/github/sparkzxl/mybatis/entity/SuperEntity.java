package com.github.sparkzxl.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.sparkzxl.mybatis.constant.EntityConstant;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class SuperEntity<T> implements Serializable {

    private static final long serialVersionUID = -4603650115461757622L;

    @TableId(value = EntityConstant.COLUMN_ID, type = IdType.INPUT)
    protected T id;

    @TableField(value = EntityConstant.COLUMN_CREATE_USER, fill = FieldFill.INSERT)
    protected T createUser;

    @TableField(value = EntityConstant.COLUMN_CREATE_TIME, fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

}
