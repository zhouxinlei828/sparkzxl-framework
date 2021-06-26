package com.github.sparkzxl.database.echo.aspect;

import com.github.sparkzxl.annotation.echo.EchoResult;
import com.github.sparkzxl.database.echo.core.EchoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * InjectionResult 注解的 AOP 工具
 *
 * @author zuihou
 */
@Aspect
@AllArgsConstructor
@Slf4j
public class EchoResultAspect {

    private final EchoService echoService;

    @Pointcut("@annotation(com.github.sparkzxl.annotation.echo.EchoResult)")
    public void methodPointcut() {
    }


    @Around("methodPointcut()&&@annotation(echoResult)")
    public Object interceptor(ProceedingJoinPoint pjp, EchoResult echoResult) throws Throwable {
        Object proceed = pjp.proceed();
        echoService.action(proceed, echoResult.ignoreFields());
        return proceed;
    }
}
