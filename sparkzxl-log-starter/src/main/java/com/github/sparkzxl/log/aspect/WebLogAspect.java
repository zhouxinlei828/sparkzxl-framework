package com.github.sparkzxl.log.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.log.entity.RequestErrorInfo;
import com.github.sparkzxl.log.entity.RequestInfo;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description: web请求日志切面
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class WebLogAspect {

    private final ThreadLocal<Stopwatch> stopwatchThreadLocal = new ThreadLocal<>();

    @Pointcut("@within(com.github.sparkzxl.log.annotation.WebLog)")
    public void pointCut() {

    }

    /**
     * 环绕操作
     *
     * @param proceedingJoinPoint 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Stopwatch stopwatch = get();
        stopwatch.reset();
        stopwatch.start();
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        Object result = proceedingJoinPoint.proceed();
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setIp(RequestContextHolderUtils.getIpAddress());
        requestInfo.setUrl(httpServletRequest.getRequestURL().toString());
        requestInfo.setHttpMethod(httpServletRequest.getMethod());
        requestInfo.setClassMethod(String.format("%s.%s", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName()));
        requestInfo.setRequestParams(getRequestParameterJson(proceedingJoinPoint.getSignature(), proceedingJoinPoint.getArgs()));
        requestInfo.setResult(result);
        String timeCost = String.valueOf(get().elapsed(TimeUnit.MILLISECONDS)).concat("毫秒");
        requestInfo.setTimeCost(timeCost);
        String jsonStr = JsonUtil.toJson(requestInfo);
        log.info("Request Info : {}", jsonStr);
        get().stop();
        return result;
    }

    /**
     * 后置通知
     */
    @AfterReturning("pointCut()")
    public void afterReturning() {
        remove();
    }

    /**
     * 异常通知，拦截记录异常日志
     */
    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e) {
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        RequestErrorInfo requestErrorInfo = new RequestErrorInfo();
        requestErrorInfo.setIp(RequestContextHolderUtils.getIpAddress());
        requestErrorInfo.setUrl(httpServletRequest.getRequestURL().toString());
        requestErrorInfo.setHttpMethod(httpServletRequest.getMethod());
        requestErrorInfo.setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()));
        requestErrorInfo.setRequestParams(getRequestParameterJson(joinPoint.getSignature(), joinPoint.getArgs()));
        requestErrorInfo.setErrorMsg(e.getMessage());
        String error = ExceptionUtil.getMessage(e);
        requestErrorInfo.setError(error);
        requestErrorInfo.setThrowExceptionClass(e.getClass().getTypeName());
        String jsonStr = JsonUtil.toJson(requestErrorInfo);
        log.info("Error Request Info : {}", jsonStr);
        remove();
    }

    public Map<String, Object> getRequestParameterJson(Signature signature, Object[] args) {
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        Map<String, Object> parameterMap = Maps.newHashMap();
        if (args != null && paramNames != null) {
            for (int i = 0; i < args.length; i++) {
                Object value = args[i];
                if (value instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) value;
                    //获取文件名
                    value = file.getOriginalFilename();
                }
                if (value instanceof ServletRequest
                        || value instanceof ServletResponse) {
                    continue;
                }
                parameterMap.put(paramNames[i], value);
            }
        }
        return parameterMap;
    }


    public Stopwatch get() {
        Stopwatch stopwatch = stopwatchThreadLocal.get();
        if (ObjectUtils.isEmpty(stopwatch)) {
            stopwatch = Stopwatch.createStarted();
            set(stopwatch);
        }
        return stopwatch;
    }

    public void set(Stopwatch stopwatch) {
        stopwatchThreadLocal.set(stopwatch);
    }

    public void remove() {
        stopwatchThreadLocal.remove();
    }
}
