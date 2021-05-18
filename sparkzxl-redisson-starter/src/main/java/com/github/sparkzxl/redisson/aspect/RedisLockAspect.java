package com.github.sparkzxl.redisson.aspect;

import com.github.sparkzxl.redisson.annotation.RedisLock;
import com.github.sparkzxl.redisson.lock.RedisDistributedLock;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class RedisLockAspect {

    private RedisDistributedLock redisDistributedLock;

    @Autowired
    public void setRedisDistributedLock(RedisDistributedLock redisDistributedLock) {
        this.redisDistributedLock = redisDistributedLock;
    }

    @Pointcut("@annotation(redisLock)")
    public void redisLockAspect(RedisLock redisLock) {

    }

    @Around(value = "redisLockAspect(redisLock)", argNames = "proceedingJoinPoint,redisLock")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RedisLock redisLock) throws Throwable {
        String threadName = Thread.currentThread().getName();
        String keyPrefix = redisLock.keyPrefix();
        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();
        int tryCount = redisLock.tryCount();
        long sleepTime = redisLock.sleepTime();
        StringBuilder keyBuffer = new StringBuilder();
        String lockKey = null;
        try {
            lockKey = parseExpression(proceedingJoinPoint, redisLock);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        keyBuffer.append(keyPrefix);
        keyBuffer.append("_");
        keyBuffer.append(lockKey);
        String key = keyBuffer.toString();
        log.info("线程[{}] -> 要加锁的key：[{}]，value：[{}]", threadName, key, lockKey);
        Object result;
        if (redisDistributedLock.lock(key, waitTime, leaseTime, tryCount, sleepTime)) {
            log.info("线程[{}] -> 获取锁key[{}] 成功", threadName, key);
            try {
                result = proceedingJoinPoint.proceed();
            } finally {
                redisDistributedLock.releaseLock(key);
                log.info("线程[{}] -> 释放锁 key [{}]", threadName, key);
            }
        } else {
            log.info("线程[{}] -> 获取锁key[{}] 失败", threadName, key);
            throw new RuntimeException("哎呀，开了个小差，请稍后再试");
        }
        return result;
    }

    private Method getTargetMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method agentMethod = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
    }

    private String parseExpression(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws NoSuchMethodException {
        String lockParam = redisLock.lockExpression();
        Method targetMethod = getTargetMethod(joinPoint);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new MethodBasedEvaluationContext(new Object(), targetMethod, joinPoint.getArgs(),
                new DefaultParameterNameDiscoverer());
        Expression expression = parser.parseExpression(lockParam);
        return expression.getValue(context, String.class);
    }

}
