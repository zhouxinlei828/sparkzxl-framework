package com.sparksys.commons.activiti.service;

import com.sparksys.commons.activiti.dto.TaskStepDTO;

import java.util.Map;

/**
 * description: 抽象任务AbstractTaskService
 *
 * @author zhouxinlei
 * @date  2020-05-24 12:44:42
 */
public abstract class AbstractTaskService {

    /**
     * 查询是否已生成流程Id
     *
     * @param businessKey 业务主键
     * @return String
     * @throws
     * @author zhouxinlei
     * @date 2020-05-09 16:52:24
     */
    public abstract String getProcessInstanceIdByBusinessKey(Long businessKey);

    /**
     * 根据流程id获取任务id
     *
     * @param processInstanceId 流程id
     * @return String
     * @author zhouxinlei
     * @date 2020-05-09 17:21:08
     */
    public abstract String getTaskIdByProcessInstanceId(String processInstanceId);

    /**
     * 保存流程操作
     *
     * @param taskDto 流程步骤操作入参
     * @return void
     * @author zhouxinlei
     * @date 2020-05-09 17:38:11
     */
    public abstract void insertTaskStep(TaskStepDTO taskDto);

    /**
     * 为驳回后审批节点设置审批人（即为此节点最后一次的提交人）
     *
     * @param condition 条件
     * @return String
     * @author zhouxinlei
     * @date 2020-05-09 18:00:01
     */
    public abstract String findActHiTaskForAssignee(Map<String, String> condition);

    /**
     * 查询当前运行时任务id
     *
     * @param procInstId
     * @return String
     * @throws
     * @author zhouxinlei
     * @date 2020-05-09 18:02:03
     */
    public abstract String findCurrentTaskId(String procInstId);
}

