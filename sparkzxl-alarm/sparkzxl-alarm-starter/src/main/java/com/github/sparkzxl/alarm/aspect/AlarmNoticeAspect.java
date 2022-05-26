package com.github.sparkzxl.alarm.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.alarm.annotation.Alarm;
import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.handler.IAlarmVariablesHandler;
import com.github.sparkzxl.alarm.provider.AlarmTemplateProvider;
import com.github.sparkzxl.alarm.send.AlarmSender;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.ArgumentAssert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Map;

/**
 * description: 告警通知切面
 *
 * @author charles.zhou
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class AlarmNoticeAspect {

    private final static String ERROR_TEMPLATE = "\n\n<font color=\"#F37335\">异常信息:</font>\n" +
            "```java\n" +
            "#{[exception]}\n" +
            "```\n";
    private final static String TEXT_ERROR_TEMPLATE = "\n异常信息:\n" +
            "#{[exception]}";
    private final AlarmTemplateProvider alarmTemplateProvider;
    private final AlarmSender alarmSender;

    @Pointcut("@annotation(alarm)")
    public void alarmPointcut(Alarm alarm) {

    }

    public IAlarmVariablesHandler getVariablesHandler(String variablesBeanName) {
        IAlarmVariablesHandler alarmVariablesHandler = SpringContextUtils.getBean(variablesBeanName);
        ArgumentAssert.notNull(alarmVariablesHandler, "告警未指定变量处理器，请联系管理员");
        return alarmVariablesHandler;
    }

    @AfterReturning(value = "alarmPointcut(alarm)", argNames = "joinPoint,alarm")
    public void doAfterReturning(JoinPoint joinPoint, Alarm alarm) {
        String templateId = alarm.templateId();
        com.github.sparkzxl.alarm.entity.AlarmTemplate alarmTemplate = alarmTemplateProvider.loadingAlarmTemplate(templateId);
        MessageSubType messageSubType = alarm.messageType();
        AlarmRequest alarmRequest = new AlarmRequest();
        alarmRequest.setTitle(alarm.name());
        alarmRequest.setContent(alarmTemplate.getTemplateContent());
        IAlarmVariablesHandler alarmVariablesHandler = getVariablesHandler(alarm.variablesBeanName());
        Map<String, Object> alarmParamMap = alarmVariablesHandler.getVariables(joinPoint, alarm);
        alarmParamMap.put("stateColor", "#45B649");
        alarmParamMap.put("state", "成功");
        alarmRequest.setVariables(alarmParamMap);
        this.alarmSender.send(messageSubType, alarmRequest);
    }


    @AfterThrowing(pointcut = "alarmPointcut(alarm)", argNames = "joinPoint,alarm,e", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Alarm alarm, Exception e) {
        log.info("请求接口发生异常 : [{}]", e.getMessage());
        String templateId = alarm.templateId();
        com.github.sparkzxl.alarm.entity.AlarmTemplate alarmTemplate = alarmTemplateProvider.loadingAlarmTemplate(templateId);
        String templateContent = "";
        MessageSubType messageSubType = alarm.messageType();
        AlarmRequest alarmRequest = new AlarmRequest();
        alarmRequest.setTitle(alarm.name());
        if (messageSubType.equals(MessageSubType.TEXT)) {
            templateContent = alarmTemplate.getTemplateContent().concat(TEXT_ERROR_TEMPLATE);
        } else if (messageSubType.equals(MessageSubType.MARKDOWN)) {
            templateContent = alarmTemplate.getTemplateContent().concat(ERROR_TEMPLATE);
        }
        IAlarmVariablesHandler alarmVariablesHandler = getVariablesHandler(alarm.variablesBeanName());
        Map<String, Object> alarmParamMap = alarmVariablesHandler.getVariables(joinPoint, alarm);
        alarmParamMap.put("stateColor", "#FF4B2B");
        alarmParamMap.put("state", "失败");
        alarmParamMap.put("exception", ExceptionUtil.stacktraceToString(e));
        alarmRequest.setVariables(alarmParamMap);
        alarmRequest.setContent(templateContent);
        this.alarmSender.send(messageSubType, alarmRequest);
    }
}
