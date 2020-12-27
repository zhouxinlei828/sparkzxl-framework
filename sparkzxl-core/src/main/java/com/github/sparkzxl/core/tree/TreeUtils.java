package com.github.sparkzxl.core.tree;

import com.github.sparkzxl.core.utils.ListUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description：list列表转换成tree列表
 *
 * @author zhouxinlei
 * @date 2020-06-17 10:36:52
 */
public class TreeUtils {

    /**
     * 构建Tree结构
     *
     * @param treeList 入参list
     * @return List<E>
     */
    public static <E extends TreeNode<E, ? extends Serializable>> List<E> buildTree(List<E> treeList) {
        if (CollectionUtils.isEmpty(treeList)) {
            return treeList;
        }
        //记录自己是自己的父节点的id集合
        List<Serializable> selfIdEqSelfParent = new ArrayList<>();
        List<E> nodeList = ListUtils.deepCopy(treeList);
        // 为每一个节点找到子节点集合
        for (E parent : nodeList) {
            Serializable id = parent.getId();
            for (E children : nodeList) {
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

        List<? extends Serializable> allIds = nodeList.stream().map(node -> node.getId()).collect(Collectors.toList());
        for (E baseNode : nodeList) {
            if (!allIds.contains(baseNode.getParentId()) || selfIdEqSelfParent.contains(baseNode.getParentId())) {
                trees.add(baseNode);
            }
        }
        return trees;
    }
}
