package com.github.sparkzxl.alarm.aspect;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.alarm.AlarmFactoryExecute;
import com.github.sparkzxl.alarm.IAlarmAttributeGet;
import com.github.sparkzxl.alarm.annotation.Alarm;
import com.github.sparkzxl.alarm.constant.enums.MessageTye;
import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.entity.NotifyMessage;
import com.github.sparkzxl.alarm.provider.AlarmTemplateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: 接口幂等性校验切面
 *
 * @author charles.zhou
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class AlarmAspect {

    private final static String ERROR_TEMPLATE = "\n\n<font color=\"#F37335\">异常信息:</font>\n" +
            "```java\n" +
            "#{[exception]}\n" +
            "```\n";
    private final static String TEXT_ERROR_TEMPLATE = "\n异常信息:\n" +
            "#{[exception]}";
    private final static String MARKDOWN_TITLE_TEMPLATE = "# 【#{[title]}】\n" +
            "\n请求状态：<font color=\"#{[stateColor]}\">#{[state]}</font>\n\n";
    private final static String TEXT_TITLE_TEMPLATE = "【#{[title]}】\n" +
            "请求状态：#{[state]}\n";
    private final AlarmTemplateProvider alarmTemplateProvider;
    private final IAlarmAttributeGet alarmAttributeGet;

    private final ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(1, 2, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10),
                    new BasicThreadFactory.Builder().namingPattern("alarm").build());

    @Pointcut("@annotation(alarm)")
    public void alarmPointcut(Alarm alarm) {

    }

    @AfterReturning(value = "alarmPointcut(alarm)", argNames = "joinPoint,alarm")
    public void doAfterReturning(JoinPoint joinPoint, Alarm alarm) {
        String templateId = alarm.templateId();
        AlarmTemplate alarmTemplate = alarmTemplateProvider.loadingAlarmTemplate(templateId);
        String templateContent = "";
        MessageTye messageTye = alarm.messageType();
        if (messageTye.equals(MessageTye.TEXT)) {
            templateContent = TEXT_TITLE_TEMPLATE.concat(alarmTemplate.getTemplateContent());
        } else if (messageTye.equals(MessageTye.MARKDOWN)) {
            templateContent = MARKDOWN_TITLE_TEMPLATE.concat(alarmTemplate.getTemplateContent());
        }
        Map<String, Object> alarmParamMap = alarmAttributeGet.getAttributes(joinPoint, alarm);
        alarmParamMap.put("stateColor", "#45B649");
        alarmParamMap.put("state", "成功");
        sendAlarm(alarm, templateContent, alarmParamMap);
    }


    @AfterThrowing(pointcut = "alarmPointcut(alarm)", argNames = "joinPoint,alarm,e", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Alarm alarm, Exception e) {
        log.info("请求接口发生异常 : [{}]", e.getMessage());
        String templateId = alarm.templateId();
        AlarmTemplate alarmTemplate = alarmTemplateProvider.loadingAlarmTemplate(templateId);
        String templateContent = "";
        MessageTye messageTye = alarm.messageType();
        if (messageTye.equals(MessageTye.TEXT)) {
            templateContent = TEXT_TITLE_TEMPLATE.concat(alarmTemplate.getTemplateContent()).concat(TEXT_ERROR_TEMPLATE);
        } else if (messageTye.equals(MessageTye.MARKDOWN)) {
            templateContent = MARKDOWN_TITLE_TEMPLATE.concat(alarmTemplate.getTemplateContent()).concat(ERROR_TEMPLATE);
        }

        Map<String, Object> alarmParamMap = alarmAttributeGet.getAttributes(joinPoint, alarm);
        alarmParamMap.put("stateColor", "#FF4B2B");
        alarmParamMap.put("state", "失败");
        alarmParamMap.put("exception", ExceptionUtil.stacktraceToString(e));
        String finalTemplateContent = templateContent;
        CompletableFuture.runAsync(() -> sendAlarm(alarm, finalTemplateContent, alarmParamMap), threadPoolExecutor);
    }

    private void sendAlarm(Alarm alarm, String templateContent, Map<String, Object> alarmParamMap) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        String message = parser.parseExpression(templateContent, parserContext).getValue(alarmParamMap, String.class);
        MessageTye messageTye = alarm.messageType();
        NotifyMessage notifyMessage = new NotifyMessage();
        notifyMessage.setTitle(alarm.name());
        notifyMessage.setMessageTye(messageTye);
        notifyMessage.setMessage(message);
        AlarmFactoryExecute.execute(notifyMessage);
    }

    /**
     * 优先从子类获取 @CheckResult：
     * 1，若子类重写了该方法，有标记就记录日志，没标记就忽略日志
     * 2，若子类没有重写该方法，就从父类获取，父类有标记就记录日志，没标记就忽略日志
     */
    public static Alarm getTargetAnnotation(JoinPoint point) {
        try {
            Alarm annotation = null;
            if (point.getSignature() instanceof MethodSignature) {
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                annotation = AnnotationUtil.getAnnotation(method, Alarm.class);
            }
            return annotation;
        } catch (Exception e) {
            log.warn("获取 {}.{} 的 @CheckResult 注解失败", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), e);
            return null;
        }
    }

}
