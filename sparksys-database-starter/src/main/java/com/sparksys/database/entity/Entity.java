package com.sparksys.database.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparksys.database.constant.EntityConstant;

import java.time.LocalDateTime;

/**
 * description：
 *
 * @author zhouxinlei
 * @date 2020/6/17 0017
 */
public class Entity<T> extends SuperEntity<T> {

    private static final long serialVersionUID = 5169873634279173683L;

    @TableField(value = EntityConstant.COLUMN_UPDATE_USER, fill = FieldFill.INSERT_UPDATE)
    protected T updateUser;

    @TableField(value = EntityConstant.COLUMN_UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime updateTime;


    public Entity(T id, LocalDateTime createTime, T createUser, LocalDateTime updateTime, T updateUser) {
        super(id, createUser, createTime);
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    public Entity() {
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public T getUpdateUser() {
        return this.updateUser;
    }

    public Entity<T> setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Entity<T> setUpdateUser(T updateUser) {
        this.updateUser = updateUser;
        return this;
    }

    @Override
    public String toString() {
        return "Entity(super=" + super.toString() + ", updateTime=" + this.getUpdateTime() + ", updateUser=" + this.getUpdateUser() + ")";
    }

}
