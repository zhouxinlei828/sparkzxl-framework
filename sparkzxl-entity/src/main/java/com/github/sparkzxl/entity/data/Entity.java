package com.github.sparkzxl.entity.data;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.github.sparkzxl.constant.EntityConstant;

import java.time.LocalDateTime;

/**
 * description：
 *
 * @author zhouxinlei
 */
public class Entity<T> extends SuperEntity<T> {

    private static final long serialVersionUID = 8002308415399326229L;
    @TableField(value = EntityConstant.COLUMN_UPDATE_USER, fill = FieldFill.INSERT_UPDATE)
    protected T modifyUser;

    @TableField(value = EntityConstant.COLUMN_UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime modifyTime;


    public Entity(T id, LocalDateTime createTime, T createUser, LocalDateTime modifyTime, T modifyUser) {
        super(id, createUser, createTime);
        this.modifyTime = modifyTime;
        this.modifyUser = modifyUser;
    }

    public Entity() {
    }

    public LocalDateTime getModifyTime() {
        return this.modifyTime;
    }

    public Entity<T> setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
        return this;
    }

    public T getModifyUser() {
        return this.modifyUser;
    }

    public Entity<T> setModifyUser(T modifyUser) {
        this.modifyUser = modifyUser;
        return this;
    }

    @Override
    public String toString() {
        return "Entity(super=" + super.toString() + ", modifyTime=" + this.getModifyTime() + ", modifyUser=" + this.getModifyUser() + ")";
    }

}
