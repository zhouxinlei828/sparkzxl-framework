package com.github.sparkzxl.database.aspect;

import com.github.sparkzxl.cache.template.GeneralCacheService;
import com.github.sparkzxl.annotation.ApiIdempotent;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * description: 接口幂等性校验切面
 *
 * @author charles.zhou
 */
@Aspect
@Slf4j
public class ApiIdempotentAspect {

    private final GeneralCacheService generalCacheService;
    private LockKeyGenerator lockKeyGenerator;

    public ApiIdempotentAspect(GeneralCacheService generalCacheService) {
        this.generalCacheService = generalCacheService;
    }

    @Pointcut("@annotation(com.github.sparkzxl.annotation.ApiIdempotent)")
    public void executeApiIdempotent() {

    }

    public void setLockKeyGenerator(LockKeyGenerator lockKeyGenerator) {
        this.lockKeyGenerator = lockKeyGenerator;
    }

    @Around("executeApiIdempotent()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        //获取方法
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        //获取幂等注解
        ApiIdempotent apiIdempotent = method.getAnnotation(ApiIdempotent.class);
        if (StringUtils.isEmpty(apiIdempotent.prefix())) {
            ExceptionAssert.failure(ApiResponseStatus.PARAM_VALID_ERROR.getCode(), "lock key don't null...");
        }
        String message = apiIdempotent.message();
        final String lockKey = lockKeyGenerator.getLockKey(joinPoint);
        log.info("接口[{}]幂等性校验，key:[{}]", httpServletRequest.getRequestURL().toString(), lockKey);
        boolean success = generalCacheService.setIfAbsent(lockKey, lockKey, apiIdempotent.expireMillis(), apiIdempotent.timeUnit());
        Object result = null;
        if (success) {
            result = joinPoint.proceed();
        } else {
            log.error("Idempotent hits, key：[{}], error msg：[{}]" + lockKey, message);
            ExceptionAssert.failure(ApiResponseStatus.FAILURE.getCode(), message);
        }
        return result;
    }

}
