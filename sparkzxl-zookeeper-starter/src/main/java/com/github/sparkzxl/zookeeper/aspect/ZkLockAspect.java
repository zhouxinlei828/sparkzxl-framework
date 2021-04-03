package com.github.sparkzxl.zookeeper.aspect;

import com.github.sparkzxl.zookeeper.annotation.ZkLock;
import com.github.sparkzxl.zookeeper.lock.ZkDistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description: zk锁
 *
 * @author zhouxinlei
 */
@Aspect
public class ZkLockAspect {

    @Autowired
    private ZkDistributedLock zkDistributedLock;

    @Pointcut("@annotation(zkLock)")
    public void zkLockAspect(ZkLock zkLock) {

    }

    @Around(value = "zkLockAspect(zkLock)", argNames = "proceedingJoinPoint,zkLock")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, ZkLock zkLock) {

        String keyPrefix = zkLock.keyPrefix();
        int lockFiled = zkLock.lockFiled();
        Object[] args = proceedingJoinPoint.getArgs();
        Object arg = args[lockFiled];
        String lockKey = keyPrefix.concat("_").concat(String.valueOf(arg));
        boolean lock = false;
        Object result = null;
        try {
            lock = zkDistributedLock.lock(lockKey);
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (lock) {
                zkDistributedLock.releaseLock(lockKey);
            }
        }
        return result;
    }
}
