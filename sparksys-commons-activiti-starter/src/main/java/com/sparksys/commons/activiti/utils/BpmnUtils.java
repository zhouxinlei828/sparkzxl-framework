package com.sparksys.commons.activiti.utils;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description：
 * @company： farben.Technology Co., Ltd.
 * @author： zhouxinlei
 * @date： 2020-07-16 12:58:22
 * @version： v1.0.0
*/
public class BpmnUtils {

    private RepositoryService repositoryService;

    private void getActivitiBPMN(String processDefinitionId) throws Exception {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        String resourceName = processDefinition.getResourceName();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                resourceName);
        //创建转换对象
        BpmnXMLConverter converter = new BpmnXMLConverter();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        //createXmlStreamReader
        XMLStreamReader reader = factory.createXMLStreamReader(resourceAsStream);
        //将xml文件转换成BpmnModel
        BpmnModel bpmnModel = converter.convertToBpmnModel(reader);
        Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElementList = (List<FlowElement>) process.getFlowElements();
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        for (FlowElement flowElement : flowElementList) {
            flowElementMap.put(flowElement.getId(), flowElement);
        }
        System.out.println(bpmnModel);

    }
}
