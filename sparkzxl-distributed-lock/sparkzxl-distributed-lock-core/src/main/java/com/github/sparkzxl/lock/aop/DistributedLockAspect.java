package com.github.sparkzxl.lock.aop;

import com.github.sparkzxl.lock.LockFailureStrategy;
import com.github.sparkzxl.lock.LockInfo;
import com.github.sparkzxl.lock.LockKeyBuilder;
import com.github.sparkzxl.lock.LockTemplate;
import com.github.sparkzxl.lock.annotation.DistributedLock;
import com.github.sparkzxl.lock.autoconfigure.DistributedLockProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * description: 分布式锁切面类
 *
 * @author zhouxinlei
 * @since 2022-05-03 10:19:11
 */
@Aspect
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final LockTemplate lockTemplate;

    private final LockKeyBuilder lockKeyBuilder;

    private final LockFailureStrategy lockFailureStrategy;

    private final DistributedLockProperties distributedLockProperties;

    @Pointcut("@annotation(distributedLock)")
    public void distributedLockPointcut(DistributedLock distributedLock) {

    }

    @Around(value = "distributedLockPointcut(distributedLock)", argNames = "proceedingJoinPoint,distributedLock")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, DistributedLock distributedLock) throws Throwable {
        LockInfo lockInfo = null;
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        try {
            String prefix = distributedLockProperties.getLockKeyPrefix() + ":";
            prefix += StringUtils.hasText(distributedLock.name()) ? distributedLock.name() :
                    method.getDeclaringClass().getName() + method.getName();
            String key = prefix + "#" + lockKeyBuilder.buildKey(proceedingJoinPoint, distributedLock.keys());
            lockInfo = lockTemplate.lock(key, distributedLock.expire(), distributedLock.acquireTimeout(), distributedLock.executor());
            if (null != lockInfo) {
                log.info("Thread[{}] -> get lock key[{}] success", lockInfo.getThreadId(), key);
                return proceedingJoinPoint.proceed();
            }
            // lock failure
            lockFailureStrategy.onLockFailure(key, method, method.getParameters());
            return null;
        } finally {
            if (null != lockInfo && distributedLock.autoRelease()) {
                log.info("Thread[{}] -> releaseLock key [{}]", lockInfo.getThreadId(), lockInfo.getLockKey());
                final boolean releaseLock = lockTemplate.releaseLock(lockInfo);
                if (!releaseLock) {
                    log.error("releaseLock fail,lockKey={},lockValue={}", lockInfo.getLockKey(),
                            lockInfo.getLockValue());
                }
            }
        }
    }

}
