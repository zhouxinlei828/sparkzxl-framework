package com.sparksys.commons.activiti.utils;

import com.sparksys.commons.activiti.entity.TaskDefineProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * description: 任务节点工具类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:45:26
 */
@Service
public class ActTaskUtil {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 当前节点属性集
     *
     * @param taskDefinitionKey 当前节点Id
     * @param nodeTaskJson      节点JsonArray数据
     * @return 节点类 TaskDefinePropDto集合
     */
    public List<TaskDefineProperty> claimByProcess(String taskDefinitionKey, String nodeTaskJson) throws JsonProcessingException {
        List<TaskDefineProperty> taskDefineProperties = objectMapper.readValue(nodeTaskJson,
                new TypeReference<List<TaskDefineProperty>>() {
                });
        for (TaskDefineProperty taskDefineProperty : taskDefineProperties) {
            List<TaskDefineProperty> nextTaskIds = objectMapper.readValue(taskDefineProperty.getNextTaskIdJson(),
                    new TypeReference<List<TaskDefineProperty>>() {
                    });
            taskDefineProperty.setNextTaskIds(nextTaskIds);
        }
        return taskDefineProperties;
    }
}
