package com.github.sparkzxl.core.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.core.json.JsonUtils;
import com.google.common.collect.Maps;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * description: 切面工具类
 *
 * @author charles.zhou
 */
public class AopUtil {

    public static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static Method getTargetMethod(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return methodSignature.getMethod();
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

    /**
     * 获取切面注解对应参数上指定注解的集合
     *
     * @param invocation   方法调用
     * @param annotation   参数注解
     * @param propertyName 注解属性
     * @return Map<String, Object>
     */
    public static Map<String, Object> getParameterAnnotationMap(MethodInvocation invocation, Class<? extends Annotation> annotation, String propertyName) {
        return getParameterAnnotationMap(invocation.getMethod(), invocation.getArguments(), annotation, propertyName);
    }


    public static Map<String, Object> getParameterAnnotationMap(Method method, Object[] args, Class<? extends Annotation> annotation, String propertyName) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Map<String, Object> paramMap = Maps.newHashMap();
        final Parameter[] parameters = method.getParameters();
        if (args != null && parameterNames != null) {
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                boolean hasAnnotation = AnnotationUtil.hasAnnotation(parameter, annotation);
                if (hasAnnotation) {
                    if (StringUtils.isNotEmpty(propertyName)) {
                        String annotationValue = AnnotationUtil.getAnnotationValue(parameter, annotation, propertyName);
                        ArgumentAssert.notNull(annotationValue, "注解【{}】对应属性值不存在，请检查注解参数是否正确设置", annotation.getSimpleName());
                        paramMap.put(annotationValue, args[i]);
                    } else {
                        paramMap.put(parameterNames[i], args[i]);
                    }
                }
            }
            if (MapUtil.isEmpty(paramMap)) {
                final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < parameterAnnotations.length; i++) {
                    final Object object = args[i];
                    final Field[] fields = object.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        boolean hasAnnotation = AnnotationUtil.hasAnnotation(field, annotation);
                        if (hasAnnotation) {
                            field.setAccessible(true);
                            if (StringUtils.isNotEmpty(propertyName)) {
                                String annotationValue = AnnotationUtil.getAnnotationValue(field, annotation, propertyName);
                                ArgumentAssert.notNull(annotationValue, "注解【{}】对应属性值不存在，请检查注解参数是否正确设置", annotation.getSimpleName());
                                paramMap.put(annotationValue, ReflectionUtils.getField(field, object));
                            } else {
                                paramMap.put(parameterNames[i], ReflectionUtils.getField(field, object));
                            }
                        }
                    }
                }
            }
        }
        return paramMap;
    }

    public static Object getParameterAnnotationData(Method method, Object[] args, Class<? extends Annotation> annotation, String propertyName) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Map<String, Object> paramMap = Maps.newHashMap();
        final Parameter[] parameters = method.getParameters();
        if (args != null && parameterNames != null) {
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                boolean hasAnnotation = AnnotationUtil.hasAnnotation(parameter, annotation);
                if (hasAnnotation) {
                    return args[i];
                }
            }
            if (MapUtil.isEmpty(paramMap)) {
                final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < parameterAnnotations.length; i++) {
                    final Object object = args[i];
                    final Field[] fields = object.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        boolean hasAnnotation = AnnotationUtil.hasAnnotation(field, annotation);
                        if (hasAnnotation) {
                            field.setAccessible(true);
                            return ReflectionUtils.getField(field, object);
                        }
                    }
                }
            }
        }
        return paramMap;
    }

    /**
     * 获取切面方法JSON数据
     *
     * @param method 方法
     * @param args   参数
     * @return String
     */
    public static String getParameterJson(Method method, Object[] args) {
        Map<String, Object> parameterMap = getParameterMap(method, args, null);
        return JsonUtils.getJson().toJsonPretty(parameterMap);
    }

    /**
     * 获取切面参数Map
     *
     * @param joinPoint    切入点
     * @param args         参数
     * @param excludeClass 排除类
     * @return Map<String, Object>
     */
    public static Map<String, Object> getParameterMap(JoinPoint joinPoint, Object[] args, Class<?>[] excludeClass) {
        Method method = getTargetMethod(joinPoint);
        return getParameterMap(method, args, excludeClass);
    }

    /**
     * 获取切面方法JSON数据
     *
     * @param method 方法
     * @param args   参数
     * @return String
     */
    public static Map<String, Object> getParameterMap(Method method, Object[] args, Class<?>[] excludeClass) {
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        Map<String, Object> parameterMap = Maps.newHashMap();
        if (args != null && paramNames != null) {
            for (int i = 0; i < args.length; i++) {
                Object value = args[i];
                if (value instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) value;
                    //获取文件名
                    value = file.getOriginalFilename();
                }
                if (value instanceof ServletRequest
                        || value instanceof ServletResponse) {
                    continue;
                }
                if (excludeClass != null && excludeClass.length > 0) {
                    List<Class<?>> classList = Arrays.asList(excludeClass);
                    if (CollectionUtils.isNotEmpty(classList)) {
                        Object finalValue = value;
                        boolean anyMatch = classList.stream().anyMatch(x -> x.getName().equals(finalValue.getClass().getName()));
                        if (anyMatch) {
                            continue;
                        }
                    }
                }
                parameterMap.put(paramNames[i], value);
            }
        }
        return parameterMap;
    }

}
