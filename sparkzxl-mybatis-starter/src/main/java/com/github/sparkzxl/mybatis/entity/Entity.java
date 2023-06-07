package com.github.sparkzxl.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.github.sparkzxl.mybatis.constant.EntityConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * description：
 *
 * @author zhouxinlei
 */
@Getter
@Setter
@ToString(callSuper = true)
public class Entity<T> extends SuperEntity<T> {

    @ApiModelProperty("更新人id")
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private T updatedBy;

    @ApiModelProperty("更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public Entity(T id, T createdBy, LocalDateTime createdAt,
                  T updatedBy, LocalDateTime updatedAt) {
        super(id, createdBy, createdAt);
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }
}
