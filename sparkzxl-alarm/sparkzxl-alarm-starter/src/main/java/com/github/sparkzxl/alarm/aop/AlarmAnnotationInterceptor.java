package com.github.sparkzxl.alarm.aop;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.alarm.annotation.Alarm;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.handler.IAlarmVariablesHandler;
import com.github.sparkzxl.alarm.provider.AlarmTemplateProvider;
import com.github.sparkzxl.alarm.send.AlarmSender;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.ArgumentAssert;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * description: 告警AOP拦截器
 *
 * @author zhouxinlei
 * @since 2022-05-27 09:16:40
 */
@Slf4j
public class AlarmAnnotationInterceptor implements MethodInterceptor {

    private final AlarmTemplateProvider alarmTemplateProvider;
    private final AlarmSender alarmSender;

    public AlarmAnnotationInterceptor(AlarmTemplateProvider alarmTemplateProvider, AlarmSender alarmSender) {
        this.alarmTemplateProvider = alarmTemplateProvider;
        this.alarmSender = alarmSender;
    }

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        //fix 使用其他aop组件时,aop切了两次.
        Class<?> cls = AopProxyUtils.ultimateTargetClass(Objects.requireNonNull(invocation.getThis()));
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        Object proceed = null;
        Alarm annotation = invocation.getMethod().getAnnotation(Alarm.class);
        String templateId = annotation.templateId();
        AlarmTemplate alarmTemplate = alarmTemplateProvider.loadingAlarmTemplate(templateId);
        StringBuilder templateContentBuilder = new StringBuilder(alarmTemplate.getTemplateContent());
        MessageSubType messageSubType = annotation.messageType();
        AlarmRequest alarmRequest = new AlarmRequest();
        alarmRequest.setTitle(annotation.name());
        alarmRequest.setContent(alarmTemplate.getTemplateContent());
        IAlarmVariablesHandler alarmVariablesHandler = getVariablesHandler(annotation.variablesBeanName());
        Map<String, Object> alarmParamMap = alarmVariablesHandler.getVariables(invocation, annotation);
        if (messageSubType.equals(MessageSubType.TEXT)) {
            templateContentBuilder.append(AlarmConstant.TEXT_HTTP_STATUS_TEMPLATE);
        } else if (messageSubType.equals(MessageSubType.MARKDOWN)) {
            templateContentBuilder.append(AlarmConstant.MARKDOWN_HTTP_STATUS_TEMPLATE);
        }
        try {
            proceed = invocation.proceed();
            alarmParamMap.put("state", "✅");
        } catch (Throwable e) {
            log.info("请求接口发生异常 : [{}]", e.getMessage());
            if (messageSubType.equals(MessageSubType.TEXT)) {
                templateContentBuilder.append(AlarmConstant.TEXT_ERROR_TEMPLATE);
            } else if (messageSubType.equals(MessageSubType.MARKDOWN)) {
                templateContentBuilder.append(AlarmConstant.MARKDOWN_ERROR_TEMPLATE);
            }
            alarmParamMap.put("state", "❌");
            alarmParamMap.put("exception", ExceptionUtil.stacktraceToString(e));
        }
        alarmRequest.setContent(templateContentBuilder.toString());
        alarmRequest.setVariables(alarmParamMap);
        this.alarmSender.send(messageSubType, alarmRequest);
        return proceed;
    }

    private IAlarmVariablesHandler getVariablesHandler(String variablesBeanName) {
        IAlarmVariablesHandler alarmVariablesHandler = SpringContextUtils.getBean(variablesBeanName);
        ArgumentAssert.notNull(alarmVariablesHandler, "告警未指定变量处理器，请联系管理员");
        return alarmVariablesHandler;
    }

}
