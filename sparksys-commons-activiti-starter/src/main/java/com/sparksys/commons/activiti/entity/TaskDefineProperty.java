package com.sparksys.commons.activiti.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description: 下一节点实体 属性
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:44:17
 */
@Data
public class TaskDefineProperty implements Serializable {


    private static final long serialVersionUID = 3046556938498382481L;
    /**
     * 当前节点id
     */
    private String taskDefId;
    /**
     * 判断是否进入该节点条件
     */
    private String actType;
    /**
     * 建议分类
     */
    private String classify;
    /**
     * 节点属性
     */
    private String taskDefType;

    /**
     * 下一节点实体集
     */
    private List<TaskDefineProperty> nextTaskIds;

    private String nextTaskIdJson;
}
