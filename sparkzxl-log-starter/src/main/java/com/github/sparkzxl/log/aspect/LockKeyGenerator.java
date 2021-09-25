package com.github.sparkzxl.log.aspect;

import com.github.sparkzxl.log.annotation.HttpRequestLog;
import com.github.sparkzxl.log.annotation.RequestLogParam;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * description: 业务描述生成工具类
 *
 * @author zhouxinlei
 */
public class LockKeyGenerator {

    public static String getLockKey(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HttpRequestLog httpRequestLog = method.getAnnotation(HttpRequestLog.class);
        final Object[] args = joinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();
        String value = httpRequestLog.value();
        StringBuilder builder = new StringBuilder(value);
        // TODO 默认解析方法里面带 RequestLogParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final RequestLogParam annotation = parameters[i].getAnnotation(RequestLogParam.class);
            if (annotation == null) {
                continue;
            }
            builder.append(httpRequestLog.delimiter())
                    .append(annotation.value())
                    .append(httpRequestLog.delimiter())
                    .append(args[i]);
        }
        if (StringUtils.isEmpty(builder.toString())) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final RequestLogParam annotation = field.getAnnotation(RequestLogParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    builder.append(httpRequestLog.delimiter())
                            .append(annotation.value())
                            .append(httpRequestLog.delimiter())
                            .append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return builder.toString();
    }
}
