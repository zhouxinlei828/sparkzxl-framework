package com.sparksys.commons.activiti.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * description: 任务节点类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:44:27
 */
@Data
public class TaskWork implements Serializable {


    private static final long serialVersionUID = -5034703512391964207L;
    /**
     * 节点名
     */
    private String defName;
    /**
     * 当前节点Id
     */
    private String taskDefId;
    /**
     * 角色组
     */
    private String candidateGroups;
    /**
     * 节点判断条件
     */
    private String actType;
    /**
     * 是否需要用户
     */
    private boolean needUser;
    /**
     * 是否需要角色组
     */
    private boolean needGroups;
    /**
     * 节点属性
     */
    private String taskDefType;
    /**
     * 是否能够进行建议分类
     */
    private String classify;

}
