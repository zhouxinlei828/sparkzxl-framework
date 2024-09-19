package com.github.sparkzxl.core.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description：list列表转换成tree列表
 *
 * @author zhouxinlei
 */
public class TreeUtils {

    /**
     * 构建Tree结构
     *
     * @param treeList 入参list
     * @return List<E>
     */
    public static <E extends TreeNode<E, ? extends Serializable>> List<E> buildTree(List<E> treeList) {
        if (treeList == null || treeList.isEmpty()) {
            return treeList;
        }
        //记录自己是自己的父节点的id集合
        // 使用HashMap来存储节点的ID与节点实例的映射
        Map<Serializable, E> nodeMap = new HashMap<>();
        for (E node : treeList) {
            nodeMap.put(node.getId(), node);
        }
        // 初始化根节点列表
        List<E> roots = new ArrayList<>();

        // 遍历节点列表，构建树结构
        for (E node : treeList) {
            Serializable parentId = node.getParentId();
            // 如果父节点ID不存在于map中，或者节点是自己的父节点，则认为它是根节点
            if (parentId == null || !nodeMap.containsKey(parentId) || parentId.equals(node.getId())) {
                roots.add(node);
            } else {
                // 获取父节点实例，并将当前节点添加到其子节点列表中
                E parent = nodeMap.get(parentId);
                parent.initChildren();
                parent.getChildren().add(node);
            }
        }
        return roots;
    }
}
