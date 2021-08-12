package com.github.sparkzxl.zookeeper.aspect;

import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.utils.AspectUtils;
import com.github.sparkzxl.zookeeper.annotation.ZkLock;
import com.github.sparkzxl.zookeeper.lock.ZkDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * description: zk锁
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class ZkLockAspect {

    private ZkDistributedLock zkDistributedLock;


    @Pointcut("@annotation(zkLock)")
    public void zkLockAspect(ZkLock zkLock) {

    }

    public void setZkDistributedLock(ZkDistributedLock zkDistributedLock) {
        this.zkDistributedLock = zkDistributedLock;
    }

    @Around(value = "zkLockAspect(zkLock)", argNames = "proceedingJoinPoint,zkLock")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, ZkLock zkLock) throws Throwable {
        long threadId = Thread.currentThread().getId();
        String keyPrefix = zkLock.keyPrefix();
        StringBuilder keyBuffer = new StringBuilder();
        String lockKey = null;
        try {
            lockKey = AspectUtils.parseExpression(proceedingJoinPoint, zkLock.expression());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        keyBuffer.append(keyPrefix);
        keyBuffer.append("_");
        keyBuffer.append(lockKey);
        String key = keyBuffer.toString();
        log.info("线程[{}] -> 要加锁的key：[{}]，value：[{}]", threadId, key, lockKey);
        Object result = null;
        if (zkDistributedLock.lock(key)) {
            log.info("线程[{}] -> 获取锁key[{}] 成功", threadId, key);
            try {
                result = proceedingJoinPoint.proceed();
            } finally {
                zkDistributedLock.releaseLock(key);
                log.info("线程[{}] -> 释放锁 key [{}]", threadId, key);
            }
        } else {
            log.info("线程[{}] -> 获取锁key[{}] 失败", threadId, key);
            ExceptionAssert.failure(ApiResponseStatus.FAILURE);
        }
        return result;
    }
}
