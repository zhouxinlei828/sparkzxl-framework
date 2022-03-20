package com.github.sparkzxl.log.aspect;

import com.github.sparkzxl.core.util.ListUtils;
import com.github.sparkzxl.log.annotation.HttpRequestLog;
import com.github.sparkzxl.log.annotation.RequestLogParam;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;

/**
 * description: 业务描述生成工具类
 *
 * @author zhouxinlei
 */
public class DescriptionGenerator {

    public static String get(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HttpRequestLog httpRequestLog = AnnotationUtils.getAnnotation(method, HttpRequestLog.class);
        if (httpRequestLog == null) {
            httpRequestLog = AnnotatedElementUtils.findMergedAnnotation(joinPoint.getTarget().getClass(), HttpRequestLog.class);
        }
        final Object[] args = joinPoint.getArgs();
        List<Object> argList = ListUtils.arrayToList(args);
        argList.removeAll(Collections.singleton(null));
        final Parameter[] parameters = method.getParameters();
        String value = httpRequestLog.value();
        StringBuilder builder = new StringBuilder(value);
        // TODO 默认解析方法里面带 RequestLogParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final RequestLogParam annotation = parameters[i].getAnnotation(RequestLogParam.class);
            if (annotation == null) {
                continue;
            }
            if (ObjectUtils.isNotEmpty(annotation)) {
                builder.append(httpRequestLog.delimiter())
                        .append(annotation.value())
                        .append(httpRequestLog.delimiter())
                        .append(argList.get(i));
            }
        }
        return builder.toString();
    }
}
