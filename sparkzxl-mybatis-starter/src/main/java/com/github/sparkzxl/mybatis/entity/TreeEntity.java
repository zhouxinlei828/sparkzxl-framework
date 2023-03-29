package com.github.sparkzxl.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 树形实体封装
 *
 * @author zhouxinlei
 */
public class TreeEntity<E, T extends Serializable> {

    protected T id;

    protected String label;

    protected T parentId;

    protected Integer sortNumber;

    @TableField(exist = false)
    protected List<E> children;

    public TreeEntity() {
    }

    public void initChildren() {
        if (this.getChildren() == null) {
            this.setChildren(new ArrayList<>());
        }

    }

    public T getId() {
        return this.id;
    }

    public TreeEntity<E, T> setId(T id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public TreeEntity<E, T> setLabel(String label) {
        this.label = label;
        return this;
    }

    public T getParentId() {
        return this.parentId;
    }

    public TreeEntity<E, T> setParentId(T parentId) {
        this.parentId = parentId;
        return this;
    }

    public Integer getSortNumber() {
        return this.sortNumber;
    }

    public TreeEntity<E, T> setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
        return this;
    }

    public List<E> getChildren() {
        return this.children;
    }

    public TreeEntity<E, T> setChildren(List<E> children) {
        this.children = children;
        return this;
    }

    @Override
    public String toString() {
        return "TreeEntity(super=" + super.toString() + ", label=" + this.getLabel() + ", parentId=" + this.getParentId() + ", sortNumber="
                + this.getSortNumber() + ", children=" + this.getChildren() + ")";
    }
}
