package com.sparksys.commons.activiti.dto;

import com.sparksys.commons.activiti.entity.TaskWork;
import lombok.Data;

/**
 * description: 工作流核心参数类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:43:41
 */
@Data
public class ActWorkflowDTO {

    /**
     * 流程类型
     */
    private Integer actType;

    /**
     * 流程图ID
     */
    private String bpmnId;


    /**
     * 业务主键
     */
    private Long businessKey;

    /**
     * 业务发起人
     */
    private String applyUserId;

    /**
     * 创建人
     */
    private Long userId;


    /**
     * 任务id
     */
    private String taskId;

    private String message;

    /**
     * 流程意见
     */
    private String classification;

    /**
     * 流程实例ID
     */
    private String procInstId;

    /**
     * 任务节点类
     */
    private TaskWork taskWork;
}
