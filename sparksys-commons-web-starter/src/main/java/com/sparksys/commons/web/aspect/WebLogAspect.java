package com.sparksys.commons.web.aspect;

import com.sparksys.commons.web.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparksys.commons.web.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * description: web请求日志切面
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:41:01
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    private static final String START_TIME = "request-start";

    @Pointcut("execution(* com.sparksys.*.interfaces.*..*(..))")
    public void pointCut() {
    }

    /**
     * 前置通知
     *
     * @param joinPoint 切入点
     * @return void
     */
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) throws JsonProcessingException {
        HttpServletRequest request = HttpUtils.getRequest();
        Long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);
        StringBuilder stringBuilder = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        if (args != null || args.length > 0) {
            for (Object object : args) {
                if (object != null) {
                    if (object instanceof ServletRequest
                            || object instanceof ServletResponse
                            || object instanceof MultipartFile) {
                        continue;
                    }
                    stringBuilder
                            .append(JacksonUtils.writeJsonAsString(object))
                            .append("\n").append(",");
                }
            }
        }
        if (!"".contentEquals(stringBuilder)) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        }
        String method = joinPoint.getTarget().getClass().getName().concat(".").concat(joinPoint.getSignature().getName());
        log.info("请求URL：[{}]，请求IP：[{}]", request.getRequestURL(), HttpUtils.getIpAddress());
        log.info("请求类型：[{}]，请求方法：[{}]", request.getMethod(), method);
        log.info("请求参数：{}", stringBuilder.toString());
    }

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        log.info("返回结果：[{}]", JacksonUtils.writeJsonAsString(result));
        return result;
    }

    /**
     * 后置通知
     *
     * @return void
     */
    @AfterReturning("pointCut()")
    public void afterReturning() {
        log.info("接口请求耗时：{}毫秒", interfaceConsume());
    }

    /**
     * 异常通知，拦截记录异常日志
     *
     * @return void
     */
    @AfterThrowing(pointcut = "pointCut()")
    public void afterThrowing() {
        log.info("接口请求耗时：{}毫秒", interfaceConsume());
    }

    private Long interfaceConsume() {
        HttpServletRequest request = HttpUtils.getRequest();
        Long start = (Long) request.getAttribute(START_TIME);
        Long end = System.currentTimeMillis();
        return end - start;
    }
}
