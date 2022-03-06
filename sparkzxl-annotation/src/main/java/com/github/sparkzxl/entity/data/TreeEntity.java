package com.github.sparkzxl.entity.data;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 树形实体封装
 *
 * @author zhouxinlei
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
    @TableField("sort_number")
    protected Integer sortNumber;

    /**
     * 子节点
     */
    @TableField(exist = false)
    protected List<E> children;

    public TreeEntity() {
    }

    public void initChildren() {
        if (this.getChildren() == null) {
            this.setChildren(new ArrayList<>());
        }

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
