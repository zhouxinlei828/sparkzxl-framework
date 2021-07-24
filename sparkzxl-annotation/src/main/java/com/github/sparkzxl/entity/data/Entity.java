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

    private static final long serialVersionUID = 5169873634279173683L;

    @TableField(value = EntityConstant.COLUMN_UPDATE_USER, fill = FieldFill.INSERT_UPDATE)
    protected T updateUser;

    @TableField(value = EntityConstant.COLUMN_UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
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
