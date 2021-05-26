package com.github.sparkzxl.database.aspect;

import com.github.sparkzxl.cache.template.GeneralCacheService;
import com.github.sparkzxl.core.annotation.ApiIdempotent;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.support.BizExceptionAssert;
import com.github.sparkzxl.core.utils.AspectUtils;
import com.github.sparkzxl.core.utils.BuildKeyUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * description: 接口幂等性校验切面
 *
 * @author charles.zhou
 * @date 2021-05-19 13:52:36
 */
@Aspect
@AllArgsConstructor
@Slf4j
public class ApiIdempotentAspect {

    private final GeneralCacheService generalCacheService;

    @Pointcut("@annotation(com.github.sparkzxl.core.annotation.ApiIdempotent)")
    public void executeApiIdempotent() {

    }

    @Around("executeApiIdempotent()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        //获取幂等注解
        ApiIdempotent apiIdempotent = method.getAnnotation(ApiIdempotent.class);
        String keyExpression = apiIdempotent.keyExpression();
        String keyPrefix = apiIdempotent.keyPrefix();
        String message = apiIdempotent.message();

        String uniqueValue = null;
        try {
            uniqueValue = AspectUtils.parseExpression(joinPoint, keyExpression);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        String classMethod = String.format("%s.%s", signature.getDeclaringTypeName(),
                signature.getName());

        // 根据 key前缀 + 方法签名 + keyExpression参数唯一值 构建缓存键值
        String key = BuildKeyUtils.generateKey(keyPrefix, classMethod, uniqueValue);

        boolean success = generalCacheService.setIfAbsent(key, key, apiIdempotent.expireMillis(), apiIdempotent.timeUnit());

        Object result = null;
        if (success) {
            result = joinPoint.proceed();
        } else {
            log.debug("Idempotent hits, key=" + key);
            BizExceptionAssert.businessFail(ApiResponseStatus.FAILURE.getCode(), message);
        }
        return result;
    }

}
