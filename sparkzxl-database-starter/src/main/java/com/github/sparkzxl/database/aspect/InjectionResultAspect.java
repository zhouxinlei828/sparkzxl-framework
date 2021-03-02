package com.github.sparkzxl.database.aspect;

import com.github.sparkzxl.database.annonation.InjectionResult;
import com.github.sparkzxl.database.injection.InjectionCore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * description: InjectionResult 注解的 AOP 工具
 *
 * @author: zhouxinlei
 * @date: 2021-03-02 13:34:58
*/
@Aspect
@AllArgsConstructor
@Slf4j
public class InjectionResultAspect {

    private final InjectionCore injectionCore;


    @Pointcut("@annotation(com.github.sparkzxl.database.annonation.InjectionResult)")
    public void methodPointcut() {
    }


    @Around("methodPointcut()&&@annotation(injectionResult)")
    public Object interceptor(ProceedingJoinPoint joinPoint, InjectionResult injectionResult) throws Throwable {
        Object proceed = joinPoint.proceed();
        try {
            injectionCore.injection(proceed, injectionResult.isUseCache());
        } catch (Exception e) {
            log.error("AOP拦截@RemoteResult出错", e);
        }
        return proceed;
    }
}
