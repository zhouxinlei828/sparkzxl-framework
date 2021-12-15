package com.github.sparkzxl.idempotent.support;

import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.generator.CacheKeyGenerator;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.idempotent.annotation.ApiIdempotent;
import com.github.sparkzxl.idempotent.constant.IdempotentConstant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class IdempotentInfoParser {

    private final Map<String, CacheKeyGenerator> keyGeneratorMap;

    public IdempotentInfo parseIdempotentInfo(ProceedingJoinPoint joinPoint) {
        //获取方法
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        //获取幂等注解
        ApiIdempotent apiIdempotent = method.getAnnotation(ApiIdempotent.class);
        if (StringUtils.isEmpty(apiIdempotent.keyPrefix())) {
            ExceptionAssert.failure(ResponseInfoStatus.PARAM_VALID_ERROR.getCode(), "lock keyPrefix don't null...");
        }
        String message = apiIdempotent.message();
        CacheKeyGenerator cacheKeyGenerator = StringUtils.isNotBlank(apiIdempotent.generator()) ? keyGeneratorMap.get(apiIdempotent.generator())
                : keyGeneratorMap.get(IdempotentConstant.DEFAULT_LOCK_KEY_GENERATOR_NAME);
        String lockKey = cacheKeyGenerator.getLockKey(joinPoint);
        return new IdempotentInfo(lockKey, apiIdempotent.maxLockMilli(), message);
    }
}
