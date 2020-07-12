package com.sparksys.commons.activiti.api;

import com.sparksys.commons.activiti.constant.ActWorkflowConstant;
import com.sparksys.commons.activiti.dto.ActWorkflowDTO;
import com.sparksys.commons.activiti.dto.TaskStepDTO;
import com.sparksys.commons.activiti.entity.TaskDefineProperty;
import com.sparksys.commons.activiti.service.AbstractTaskService;
import com.sparksys.commons.activiti.utils.ActTaskUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: activiti 工作流核心操作API
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:43:00
 */
@Component
@Slf4j
public class ActWorkflowApi {

    @Resource
    private AbstractTaskService abstractTaskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ActTaskUtil actTaskUtil;

    public int driveWorkflowProcess(ActWorkflowDTO actWorkflowDTO) throws Exception {
        int ret;
        Map<String, Object> actWorkflowMap = new HashMap<>();
        //流程推动
        int actType = actWorkflowDTO.getActType();
        actWorkflowMap.put("actType", actType);
        String processInstanceId = abstractTaskService.getProcessInstanceIdByBusinessKey(actWorkflowDTO.getBusinessKey());
        if (ActWorkflowConstant.WorkflowAction.START == actType) {
            if (StringUtils.isNotEmpty(processInstanceId)) {
                log.error("【流程异常，已存在流程实例Id{}】", processInstanceId);
                throw new Exception("流程异常，已存在流程实例Id".concat(processInstanceId));
            }
            if (StringUtils.isNotEmpty(actWorkflowDTO.getApplyUserId())) {
                actWorkflowMap.put("applyUserId", actWorkflowDTO.getApplyUserId());
            }
            ret = startRuntimeProcess(actWorkflowDTO.getBpmnId(),
                    actWorkflowDTO.getUserId(),
                    actWorkflowDTO.getBusinessKey(),
                    actWorkflowDTO.getMessage(),
                    actWorkflowMap,
                    actWorkflowDTO);
        } else if (actType < 0) {
            actWorkflowMap.put("nextTaskDefType", actWorkflowDTO.getTaskWork().getTaskDefType());
            actWorkflowMap.put("nextTaskDefId", actWorkflowDTO.getTaskWork().getTaskDefId());
            actWorkflowMap.put("needUser", actWorkflowDTO.getTaskWork().isNeedUser());
            actWorkflowMap.put("procInstId", actWorkflowDTO.getProcInstId());
            ret = this.rollbackFlow(actWorkflowDTO.getTaskId(), actWorkflowDTO.getUserId(), actWorkflowDTO.getMessage(),
                    actWorkflowMap);
        } else {
            if (StringUtils.isNotEmpty(actWorkflowDTO.getApplyUserId())) {
                actWorkflowMap.put("applyUserId", actWorkflowDTO.getApplyUserId());
            }
            ret = this.submitProcess(actWorkflowDTO.getTaskId(), actWorkflowDTO.getUserId(), actWorkflowDTO.getMessage(),
                    actWorkflowMap, actWorkflowDTO);
        }

        return ret;
    }

    /**
     * activiti流程启动方法
     *
     * @param bpmnId         流程图ID
     * @param userId         用户ID
     * @param businessKey    业务ID
     * @param message        message
     * @param variables      act_ru_variables表参数 例如：key:业务用户ID , value:用户 ; key:input,value:true;
     * @param actWorkflowDTO 工作流核心参数
     * @return int
     * @author zhouxinlei
     * @date 2020-05-09 17:22:47
     */
    private int startRuntimeProcess(String bpmnId, Long userId,
                                    Long businessKey, String message, Map<String, Object> variables, ActWorkflowDTO actWorkflowDTO) throws Exception {
        int ret;
        identityService.setAuthenticatedUserId(String.valueOf(userId));
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(bpmnId, businessKey.toString(), variables);
        String taskId = abstractTaskService.getTaskIdByProcessInstanceId(instance.getProcessInstanceId());
        ret = promoteProcess(taskId, userId, message, variables, actWorkflowDTO);
        log.info("--------启动流程 ProcessInstance = {}--------", objectMapper.writeValueAsString(instance));
        return ret;
    }

    /**
     * 流程推动
     *
     * @param taskId         任务Id
     * @param userId         用户id
     * @param message        处理意见
     * @param variables      流程中传入动态的assignee <例如：${角色}>
     * @param actWorkflowDTO 工作流核心参数
     * @return int
     * @author zhouxinlei
     * @date 2020-05-09 17:26:10
     */
    private int promoteProcess(String taskId, Long userId, String message, Map<String, Object> variables,
                               ActWorkflowDTO actWorkflowDTO) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new Exception("任务为空！");
        }
        String processInstanceId = task.getProcessInstanceId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        //添加审核人
        Authentication.setAuthenticatedUserId(String.valueOf(userId));
        taskService.addComment(taskId, processInstanceId, message);
        //意见中的建议分类入库
        if (StringUtils.isNotEmpty(actWorkflowDTO.getClassification())) {
            taskService.setVariableLocal(taskId, "classify", actWorkflowDTO.getClassification());
        }
        taskService.claim(taskId, String.valueOf(userId));
        taskService.complete(taskId, variables);
        TaskStepDTO taskDto = new TaskStepDTO();

        taskDto.setTaskDefinitionKey(taskDefinitionKey);
        taskDto.setTaskId(taskId);
        int actType = Integer.parseInt(variables.get("actType").toString());

        //只要actType小于0的动作都显示为驳回，除了退回分发（-2）显示退回分发
        if (actType < 0 && actType != ActWorkflowConstant.WorkflowAction.CANCEL_CLAIM) {
            actType = ActWorkflowConstant.WorkflowAction.ROLLBACK;
        }
        //跨过分发直接提交至被分发人的动作仍然认为是提交
        if (actType == ActWorkflowConstant.WorkflowAction.ROUND_CLAIM) {
            actType = ActWorkflowConstant.WorkflowAction.SUBMIT;
        }
        taskDto.setActionStatus(actType);
        taskDto.setProcessInstanceId(processInstanceId);
        taskDto.setUpdateUserId(userId);
        abstractTaskService.insertTaskStep(taskDto);
        return 0;
    }


    /**
     * 流程任务提交
     *
     * @param taskId    任务Id
     * @param userId    用户 Id
     * @param variables 流程中传入动态的assignee <例如：${角色}>
     * @param message   处理意见
     * @return
     */
    private int submitProcess(String taskId, Long userId, String message, Map<String, Object> variables, ActWorkflowDTO actWorkflowDTO) throws Exception {
        int ret = 0;
        int actType = Integer.parseInt(variables.get("actType").toString());
        //如果是正常提交，需要判断下一节点是否直接到被分发人
        if (ActWorkflowConstant.WorkflowAction.SUBMIT == actType) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            //当前节点信息
            String nodeTaskJson = task.getDescription();
            //获得下一节点集合
            List<TaskDefineProperty> taskDefineProperties = actTaskUtil.claimByProcess(task.getTaskDefinitionKey(), nodeTaskJson);
            //判断下一节点是否有分发
            boolean nextHaveClaim = false;
            List<TaskDefineProperty> nextNodeState = taskDefineProperties.get(0).getNextTaskIds();
            for (TaskDefineProperty tempNextNode : nextNodeState) {
                if (ActWorkflowConstant.TASK_CLAIM.equals(tempNextNode.getTaskDefType())) {
                    nextHaveClaim = true;
                    break;
                }
            }
            //如果下一节点有分发，则判断是否直接到被分发人
            String roundClaimDefId = "";
            if (nextHaveClaim) {
                //取得下一节点actType=4的节点定义（直接被分发的任务定义ID）
                for (TaskDefineProperty tempNextNode : nextNodeState) {
                    if (ActWorkflowConstant.WorkflowAction.ROUND_CLAIM == Integer.parseInt(tempNextNode.getActType())) {
                        roundClaimDefId = tempNextNode.getTaskDefId();

                        Map<String, String> condition = new HashMap<>(2);
                        condition.put("taskDefKey", roundClaimDefId);
                        condition.put("procInstId", actWorkflowDTO.getProcInstId());
                        //为分发后审批节点设置审批人（即为此节点最后一次的提交人）
                        //如果有人员ID，则说明是驳回过的，所以直接推送到被分发人
                        String assignee = abstractTaskService.findActHiTaskForAssignee(condition);
                        if (assignee != null && !"".equals(assignee)) {
                            Map<String, Object> submitMap = new HashMap<>(2);
                            submitMap.put("actType", ActWorkflowConstant.WorkflowAction.ROUND_CLAIM);
                            submitMap.put("applyUserId", assignee);
                            ret = promoteProcess(taskId, userId, message, submitMap, actWorkflowDTO);
                            return ret;
                        }
                    }
                }
            }

        }
        return promoteProcess(taskId, userId, message, variables, actWorkflowDTO);
    }

    /**
     * 流程任务驳回
     *
     * @param taskId    任务Id
     * @param userId    用户id
     * @param message   处理意见
     * @param variables 流程中传入动态的assignee <例如：${角色}>
     * @return int
     * @throws Exception 异常
     * @author zhouxinlei
     * @date 2020-05-09 18:03:44
     */
    private int rollbackFlow(String taskId, Long userId, String message, Map<String, Object> variables) throws Exception {
        int ret = 0;
        Map<String, Object> rollBackMap = new HashMap<>(2);
        int actType = Integer.parseInt(variables.get("actType").toString());
        String nextTaskDefId = variables.get("nextTaskDefId").toString();
        String procInstId = variables.get("procInstId").toString();
        Boolean needUser = (Boolean) variables.get("needUser");
        ActWorkflowDTO actWorkflowDTO = new ActWorkflowDTO();
        rollBackMap.put("actType", actType);
        //退回分发也需要和驳回一样指定原来执行人
        Map<String, String> condition = new HashMap<>(2);
        condition.put("taskDefKey", nextTaskDefId);
        condition.put("procInstId", procInstId);
        //为驳回后审批节点设置审批人（即为此节点最后一次的提交人）
        String assignee = abstractTaskService.findActHiTaskForAssignee(condition);
        if (needUser) {
            rollBackMap.put("applyUserId", assignee);
        }
        ret = promoteProcess(taskId, userId, message, rollBackMap, actWorkflowDTO);
        String currentTaskId = abstractTaskService.findCurrentTaskId(procInstId);
        if (!needUser) {
            taskService.setAssignee(currentTaskId, assignee);
        }
        return ret;
    }
}
