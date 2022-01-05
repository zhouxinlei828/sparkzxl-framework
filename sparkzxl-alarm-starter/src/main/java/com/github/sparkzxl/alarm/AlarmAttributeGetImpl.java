package com.github.sparkzxl.alarm;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONArray;
import com.github.sparkzxl.alarm.annotation.Alarm;
import com.github.sparkzxl.alarm.aspect.AlarmParamGenerator;
import com.github.sparkzxl.alarm.entity.ExpressionTemplate;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * description: 获取告警变量参数实现类
 *
 * @author zhouxinlei
 * @date 2022-01-05 12:32:01
 */
public class AlarmAttributeGetImpl implements IAlarmAttributeGet {

    private final Function<String, String> function;

    public AlarmAttributeGetImpl(Function<String, String> function) {
        this.function = function;
    }

    @Override
    public Map<String, Object> getAttributes(JoinPoint joinPoint, Alarm alarm) {
        Map<String, Object> attributeMapping = new HashMap<>(6);
        attributeMapping.put("title", alarm.name());
        Map<String, Object> alarmParamMap = AlarmParamGenerator.generate(joinPoint);
        if (MapUtil.isNotEmpty(alarmParamMap)) {
            attributeMapping.putAll(alarmParamMap);
        }
        String extractParams = alarm.extractParams();
        if (StringUtils.isNotEmpty(extractParams)) {
            String[] headerArray = StringUtils.split(extractParams, ",");
            for (String header : headerArray) {
                attributeMapping.put(header, function.apply(header));
            }
        }
        String expressionJson = alarm.expressionJson();
        if (StringUtils.isNotBlank(expressionJson)) {
            List<ExpressionTemplate> expressionTemplateList = JSONArray.parseArray(expressionJson, ExpressionTemplate.class);
            for (ExpressionTemplate expressionTemplate : expressionTemplateList) {
                try {
                    String value = parseExpression(joinPoint, expressionTemplate.getExpression());
                    System.out.println(value);
                    attributeMapping.put(expressionTemplate.getKey(), value);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return attributeMapping;
    }

    public static Method getTargetMethod(JoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method agentMethod = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
    }

    /**
     * 获取spel表达式结果
     *
     * @param joinPoint  切入点
     * @param expression 表达式
     * @return String
     * @throws NoSuchMethodException 方法找不到异常
     */
    public static String parseExpression(JoinPoint joinPoint, String expression) throws NoSuchMethodException {
        Method targetMethod = getTargetMethod(joinPoint);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new MethodBasedEvaluationContext(new Object(), targetMethod, joinPoint.getArgs(),
                new DefaultParameterNameDiscoverer());
        Expression parseExpression = parser.parseExpression(expression);
        return parseExpression.getValue(context, String.class);
    }
}
