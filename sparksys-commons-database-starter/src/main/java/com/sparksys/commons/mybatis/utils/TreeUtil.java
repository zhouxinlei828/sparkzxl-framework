package com.sparksys.commons.mybatis.utils;

import com.sparksys.commons.mybatis.entity.TreeEntity;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description：list列表 转换成tree列表
 *
 * @author zhouxinlei
 * @date 2020-06-17 10:36:52
 */
public class TreeUtil {

    /**
     * 构建Tree结构
     *
     * @param treeList
     * @param <E>      实体
     * @param <?>      主键
     * @return
     */
    public static <E extends TreeEntity<E, ? extends Serializable>> List<E> buildTree(List<E> treeList) {
        if (CollectionUtils.isEmpty(treeList)) {
            return treeList;
        }
        //记录自己是自己的父节点的id集合
        List<Serializable> selfIdEqSelfParent = new ArrayList<>();
        // 为每一个节点找到子节点集合
        for (E parent : treeList) {
            Serializable id = parent.getId();
            for (E children : treeList) {
                if (parent != children) {
                    //parent != children 这个来判断自己的孩子不允许是自己，因为有时候，根节点的parent会被设置成为自己
                    if (id.equals(children.getParentId())) {
                        parent.initChildren();
                        parent.getChildren().add(children);
                    }
                } else if (id.equals(parent.getParentId())) {
                    selfIdEqSelfParent.add(id);
                }
            }
        }
        // 找出根节点集合
        List<E> trees = new ArrayList<>();

        List<? extends Serializable> allIds = treeList.stream().map(node -> node.getId()).collect(Collectors.toList());
        for (E baseNode : treeList) {
            if (!allIds.contains(baseNode.getParentId()) || selfIdEqSelfParent.contains(baseNode.getParentId())) {
                trees.add(baseNode);
            }
        }
        return trees;
    }
}
