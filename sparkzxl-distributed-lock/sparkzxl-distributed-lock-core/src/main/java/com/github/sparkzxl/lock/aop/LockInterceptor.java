package com.github.sparkzxl.lock.aop;

import com.github.sparkzxl.lock.LockFailureStrategy;
import com.github.sparkzxl.lock.LockInfo;
import com.github.sparkzxl.lock.LockKeyBuilder;
import com.github.sparkzxl.lock.LockTemplate;
import com.github.sparkzxl.lock.annotation.DistributedLock;
import com.github.sparkzxl.lock.autoconfigure.LockProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.StringUtils;


/**
 * description: 分布式锁Aop处理器
 *
 * @author zhouxinlei
 * @since 2022-05-01 22:00:08
 */
@Slf4j
@RequiredArgsConstructor
public class LockInterceptor implements MethodInterceptor {

    private final LockTemplate lockTemplate;

    private final LockKeyBuilder lockKeyBuilder;

    private final LockFailureStrategy lockFailureStrategy;

    private final LockProperties lock4jProperties;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //fix 使用其他aop组件时,aop切了两次.
        Class<?> cls = AopProxyUtils.ultimateTargetClass(invocation.getThis());
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        DistributedLock distributedLock = invocation.getMethod().getAnnotation(DistributedLock.class);
        LockInfo lockInfo = null;
        try {
            String prefix = lock4jProperties.getLockKeyPrefix() + ":";
            prefix += StringUtils.hasText(distributedLock.name()) ? distributedLock.name() :
                    invocation.getMethod().getDeclaringClass().getName() + invocation.getMethod().getName();
            String key = prefix + "#" + lockKeyBuilder.buildKey(invocation, distributedLock.keys());
            lockInfo = lockTemplate.lock(key, distributedLock.expire(), distributedLock.acquireTimeout(), distributedLock.executor());
            if (null != lockInfo) {
                return invocation.proceed();
            }
            // lock failure
            lockFailureStrategy.onLockFailure(key, invocation.getMethod(), invocation.getArguments());
            return null;
        } finally {
            if (null != lockInfo && distributedLock.autoRelease()) {
                final boolean releaseLock = lockTemplate.releaseLock(lockInfo);
                if (!releaseLock) {
                    log.error("releaseLock fail,lockKey={},lockValue={}", lockInfo.getLockKey(),
                            lockInfo.getLockValue());
                }
            }
        }
    }

}
