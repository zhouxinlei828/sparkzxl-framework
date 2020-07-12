package com.sparksys.commons.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * description: 树形实体封装
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:48:27
 */
public class TreeEntity<E, T extends Serializable> extends Entity<T> {

    /**
     * 名称
     */
    @TableField(value = "label", condition = "%s LIKE CONCAT('%%',#{%s},'%%')")
    protected String label;

    /**
     * 父ID
     */
    @TableField("parent_id")
    protected T parentId;

    /**
     * 排序号
     */
    @TableField("sort_value")
    protected Integer sortValue;

    /**
     * 子节点
     */
    @TableField(exist = false)
    protected List<E> children;

    public TreeEntity() {
    }

    public void initChildren() {
        if (this.getChildren() == null) {
            this.setChildren(Lists.newArrayList());
        }

    }

    public String getLabel() {
        return this.label;
    }

    public T getParentId() {
        return this.parentId;
    }

    public Integer getSortValue() {
        return this.sortValue;
    }

    public List<E> getChildren() {
        return this.children;
    }

    public TreeEntity<E, T> setLabel(String label) {
        this.label = label;
        return this;
    }

    public TreeEntity<E, T> setParentId(T parentId) {
        this.parentId = parentId;
        return this;
    }

    public TreeEntity<E, T> setSortValue(Integer sortValue) {
        this.sortValue = sortValue;
        return this;
    }

    public TreeEntity<E, T> setChildren(List<E> children) {
        this.children = children;
        return this;
    }

    @Override
    public String toString() {
        return "TreeEntity(super=" + super.toString() + ", label=" + this.getLabel() + ", parentId=" + this.getParentId() + ", sortValue=" + this.getSortValue() + ", children=" + this.getChildren() + ")";
    }
}
