package com.github.sparkzxl.web.aop;

import com.github.sparkzxl.annotation.ApiLimit;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.web.support.LimitException;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * description: ApiLimitInterceptor
 *
 * @author zhouxinlei
 * @since 2022-09-27 09:28:55
 */
@Slf4j
public class ApiLimitInterceptor implements MethodInterceptor {

    /**
     * 不同的接口，不同的流量控制，map的key为Limit.key
     */
    private final Map<String, RateLimiter> limiterMap = Maps.newConcurrentMap();

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        //fix 使用其他aop组件时,aop切了两次.
        Class<?> cls = AopProxyUtils.ultimateTargetClass(Objects.requireNonNull(invocation.getThis()));
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        ApiLimit apiLimit = invocation.getMethod().getAnnotation(ApiLimit.class);
        if (apiLimit != null) {
            ArgumentAssert.notEmpty(apiLimit.key(), "限流key不能为空");
            String apiLimitKey = invocation.getMethod().getDeclaringClass().getName() + invocation.getMethod().getName().concat("#").concat(apiLimit.key());
            RateLimiter rateLimiter = limiterMap.get(apiLimitKey);
            if (null == rateLimiter) {
                //创建当前key的RateLimiter
                rateLimiter = RateLimiter.create(apiLimit.permitsPerSecond());
                limiterMap.put(apiLimitKey, rateLimiter);
                log.info("创建了新的令牌桶，key={},容量={}", apiLimitKey, apiLimit.permitsPerSecond());
            }
            boolean success = rateLimiter.tryAcquire(apiLimit.timeOut(), apiLimit.timeUnit());
            if (!success) {
                log.info("获取令牌失败,key={}", apiLimitKey);
                throw new LimitException(apiLimit.message());
            }
            log.info("获取令牌成功,key={}", apiLimitKey);
        }
        return invocation.proceed();
    }
}
