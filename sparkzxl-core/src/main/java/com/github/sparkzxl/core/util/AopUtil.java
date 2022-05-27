package com.github.sparkzxl.core.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * description: 切面工具类
 *
 * @author charles.zhou
 */
public class AopUtil {

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
        return parseExpression(targetMethod, joinPoint.getArgs(), expression);
    }

    /**
     * 获取spel表达式结果
     *
     * @param invocation 切入点
     * @param expression 表达式
     * @return String
     */
    public static String parseExpression(MethodInvocation invocation, String expression) {
        Method targetMethod = invocation.getMethod();
        return parseExpression(targetMethod, invocation.getArguments(), expression);
    }

    /**
     * 获取spel表达式结果
     *
     * @param method     方法
     * @param arguments  参数属性
     * @param expression 表达式
     * @return String
     */
    public static String parseExpression(Method method, Object[] arguments, String expression) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new MethodBasedEvaluationContext(new Object(), method, arguments,
                new DefaultParameterNameDiscoverer());
        Expression parseExpression = parser.parseExpression(expression);
        return parseExpression.getValue(context, String.class);
    }

    public static Map<String, Object> generateMap(MethodInvocation invocation, Class<? extends Annotation> annotation, String propertyName) {
        return generateMap(invocation.getMethod(), invocation.getArguments(), annotation, propertyName);
    }


    public static Map<String, Object> generateMap(JoinPoint joinPoint, Class<? extends Annotation> annotation, String propertyName) {
        Method targetMethod;
        try {
            targetMethod = getTargetMethod(joinPoint);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        final Object[] arguments = joinPoint.getArgs();
        return generateMap(targetMethod, arguments, annotation, propertyName);
    }

    public static Map<String, Object> generateMap(Method method, Object[] arguments, Class<? extends Annotation> annotation, String propertyName) {
        Map<String, Object> alarmParamMap = Maps.newHashMap();
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = arguments[i];
            String annotationValue = AnnotationUtil.getAnnotationValue(parameter, annotation, propertyName);
            if (annotationValue == null) {
                continue;
            }
            alarmParamMap.put(annotationValue, arg);
        }
        if (MapUtil.isEmpty(alarmParamMap)) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = arguments[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    String annotationValue = AnnotationUtil.getAnnotationValue(field, annotation, propertyName);
                    if (annotationValue == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    alarmParamMap.put(annotationValue, ReflectionUtils.getField(field, object));
                }
            }
        }
        return alarmParamMap;
    }

}
