package com.github.sparkzxl.core.tree;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * description: tree节点
 *
 * @author zhouxinlei
 */
public class TreeNode<E, T extends Serializable> implements Cloneable, Serializable {

    /**
     * 名称
     */
    protected String label;
    /**
     * 父ID
     */
    protected T parentId;
    /**
     * 排序号
     */
    protected Integer sortValue;
    /**
     * 子节点
     */
    protected List<E> children;
    private T id;

    public TreeNode() {
    }

    public void initChildren() {
        if (this.getChildren() == null) {
            this.setChildren(Lists.newArrayList());
        }

    }

    public T getId() {
        return this.id;
    }

    public TreeNode<E, T> setId(T id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public TreeNode<E, T> setLabel(String label) {
        this.label = label;
        return this;
    }

    public T getParentId() {
        return this.parentId;
    }

    public TreeNode<E, T> setParentId(T parentId) {
        this.parentId = parentId;
        return this;
    }

    public Integer getSortValue() {
        return this.sortValue;
    }

    public TreeNode<E, T> setSortValue(Integer sortValue) {
        this.sortValue = sortValue;
        return this;
    }

    public List<E> getChildren() {
        return this.children;
    }

    public TreeNode<E, T> setChildren(List<E> children) {
        this.children = children;
        return this;
    }

    @Override
    protected Object clone() {
        TreeNode<E, T> treeNode = null;
        try {
            treeNode = (TreeNode<E, T>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return treeNode;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "label='" + label + '\'' +
                ", parentId=" + parentId +
                ", sortValue=" + sortValue +
                ", children=" + children +
                '}';
    }
}
