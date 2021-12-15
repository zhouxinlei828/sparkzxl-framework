package com.github.sparkzxl.idempotent.aspect;

import com.github.sparkzxl.idempotent.support.IdempotentInfo;
import com.github.sparkzxl.idempotent.support.IdempotentInfoParser;
import com.github.sparkzxl.idempotent.support.IdempotentManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * description: 接口幂等性校验切面
 *
 * @author charles.zhou
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ApiIdempotentAspect {

    private final IdempotentManager idempotentManager;

    private final IdempotentInfoParser idempotentInfoParser;

    @Pointcut("@annotation(com.github.sparkzxl.idempotent.annotation.ApiIdempotent)")
    public void executeApiIdempotent() {

    }

    @Around("executeApiIdempotent()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        IdempotentInfo idempotentInfo = idempotentInfoParser.parseIdempotentInfo(joinPoint);
        boolean lock = idempotentManager.tryLock(idempotentInfo);
        if (!lock) {
            return idempotentManager.handlerNoLock(idempotentInfo);
        }
        return joinPoint.proceed();
    }

}
