package com.sparksys.commons.activiti.api;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: Activiti公共Service
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:42:29
 */
@Service
public class ActivitiService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 获取任务定义角色
     *
     * @param taskId 任务id
     * @return map
     */
    public Map<String, Object> getTaskRoleDefinition(String taskId, String nextTaskDefinitionId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        org.activiti.bpmn.model.Process process = model.getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement fle : flowElements) {
            if (fle instanceof UserTask) {
                UserTask userTask = (UserTask) fle;
                if (nextTaskDefinitionId.equals(userTask.getId())) {
                    List<String> candidateGroups = userTask.getCandidateGroups();
                    String assignee = userTask.getAssignee();
                    String name = userTask.getName();
                    Map<String, Object> map = new HashMap<>();
                    map.put("candidateGroups", candidateGroups);
                    map.put("assignee", assignee);
                    map.put("name", name);
                    return map;
                }
            }
        }
        return null;
    }
}
