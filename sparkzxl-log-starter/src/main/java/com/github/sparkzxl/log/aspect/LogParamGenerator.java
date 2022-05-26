package com.github.sparkzxl.log.aspect;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.log.annotation.LogParam;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2021-12-29 09:30
 */
public class LogParamGenerator {

    public static Map<String, Object> generate(JoinPoint joinPoint) {
        Map<String, Object> alarmParamMap = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        final Object[] args = joinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();
        // TODO 默认解析方法里面带 LogParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final LogParam annotation = parameters[i].getAnnotation(LogParam.class);
            if (annotation == null) {
                continue;
            }
            alarmParamMap.put(annotation.value(), args[i]);
        }
        if (MapUtil.isEmpty(alarmParamMap)) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final LogParam annotation = field.getAnnotation(LogParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    alarmParamMap.put(annotation.value(), ReflectionUtils.getField(field, object));
                }
            }
        }
        return alarmParamMap;
    }
}
