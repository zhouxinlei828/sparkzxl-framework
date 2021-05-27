package com.github.sparkzxl.database.aspect;

import com.github.sparkzxl.core.annotation.ApiIdempotent;
import com.github.sparkzxl.core.annotation.ApiIdempotentParam;
import com.github.sparkzxl.core.generator.CacheKeyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * description: 缓存key 生成
 *
 * @author zhouxinlei
 * @date 2021-05-27 15:29
 */
public class LockKeyGenerator implements CacheKeyGenerator {

    @Override
    public String getLockKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiIdempotent apiIdempotent = method.getAnnotation(ApiIdempotent.class);
        final Object[] args = joinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder();
        // TODO 默认解析方法里面带 ApiIdempotentParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final ApiIdempotentParam annotation = parameters[i].getAnnotation(ApiIdempotentParam.class);
            if (annotation == null) {
                continue;
            }
            builder.append(apiIdempotent.delimiter()).append(args[i]);
        }
        if (StringUtils.isEmpty(builder.toString())) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final ApiIdempotentParam annotation = field.getAnnotation(ApiIdempotentParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    builder.append(apiIdempotent.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return apiIdempotent.prefix() + builder;
    }
}
