package com.github.sparkzxl.alarm.aspect;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.alarm.annotation.AlarmParam;
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
 */
public class AlarmParamGenerator {

    public static Map<String, Object> generate(JoinPoint joinPoint) {
        Map<String, Object> alarmParamMap = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        final Object[] args = joinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();
        // TODO 默认解析方法里面带 AlarmParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            final AlarmParam annotation = parameter.getAnnotation(AlarmParam.class);
            if (annotation == null) {
                continue;
            }
            alarmParamMap.put(annotation.name(), arg);
        }
        if (MapUtil.isEmpty(alarmParamMap)) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final AlarmParam annotation = field.getAnnotation(AlarmParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    alarmParamMap.put(annotation.name(), ReflectionUtils.getField(field, object));
                }
            }
        }
        return alarmParamMap;
    }
}
