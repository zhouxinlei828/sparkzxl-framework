package com.sparksys.commons.activiti.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * description: 流程步骤操作入参
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:44:04
 */
@Data
public class TaskStepDTO implements Serializable {

    private static final long serialVersionUID = -5184589100409931358L;

    private String taskId;

    private String processInstanceId;

    private String taskDefinitionKey;

    private Integer actionStatus;

    private Long updateUserId;


}
