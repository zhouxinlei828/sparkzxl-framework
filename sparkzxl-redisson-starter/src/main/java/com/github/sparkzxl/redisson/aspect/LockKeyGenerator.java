package com.github.sparkzxl.redisson.aspect;

import com.github.sparkzxl.redisson.annotation.RedisLock;
import com.github.sparkzxl.redisson.annotation.RedisLockParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * description: 缓存key 生成
 *
 * @author zhouxinlei
 */
public class LockKeyGenerator {

    public static String getLockKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        final Object[] args = joinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder();
        // TODO 默认解析方法里面带 ApiIdempotentParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final RedisLockParam annotation = parameters[i].getAnnotation(RedisLockParam.class);
            if (annotation == null) {
                continue;
            }
            builder.append(redisLock.delimiter()).append(args[i]);
        }
        if (StringUtils.isEmpty(builder.toString())) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final RedisLockParam annotation = field.getAnnotation(RedisLockParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    builder.append(redisLock.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return redisLock.prefix() + builder;
    }
}
