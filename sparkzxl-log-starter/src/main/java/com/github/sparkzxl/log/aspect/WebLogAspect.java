package com.github.sparkzxl.log.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.core.context.BaseContextHolder;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.utils.NetworkUtil;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import com.github.sparkzxl.log.LogStoreService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    private boolean storage = false;

    @Autowired(required = false)
    private LogStoreService logStoreService;

    public void setStorage(boolean storage) {
        this.storage = storage;
    }

    @Pointcut("@within(com.github.sparkzxl.log.annotation.WebLog)")
    public void pointCut() {

    }

    /**
     * 对Controller下面的方法执行前进行切入，初始化开始时间
     *
     * @param joinPoint 切入点
     */
    @Before("pointCut()")
    public void beforeMethod(JoinPoint joinPoint) {
        Stopwatch stopwatch = get();
        if (stopwatch.isRunning()) {
            stopwatch.reset().start();
        } else {
            stopwatch.start();
        }
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        RequestInfo requestParamInfo = buildRequestParamInfo(httpServletRequest, joinPoint.getSignature(), joinPoint.getArgs());
        String jsonStr = JsonUtil.toJson(requestParamInfo);
        log.info("请求参数信息: [{}]", jsonStr);
        if (storage) {
            CompletableFuture.runAsync(() -> logStoreService.saveLog(requestParamInfo));
        }
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
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        Object result = proceedingJoinPoint.proceed();
        RequestInfo requestResultInfo = buildRequestResultInfo(httpServletRequest, proceedingJoinPoint.getSignature(), result);
        String jsonStr = JsonUtil.toJson(requestResultInfo);
        log.info("响应结果信息: [{}]", jsonStr);
        return result;
    }

    /**
     * 后置通知
     */
    @AfterReturning("pointCut()")
    public void afterReturning(JoinPoint joinPoint) {
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        String timeCost = String.valueOf(get().elapsed(TimeUnit.MILLISECONDS)).concat("毫秒");
        log.info("请求接口：[{}],总计耗时: [{}]", httpServletRequest.getRequestURL().toString(), timeCost);
        remove();
    }

    /**
     * 异常通知，拦截记录异常日志
     */
    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Exception e) {
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        RequestInfo requestInfo = buildRequestErrorInfo(httpServletRequest, joinPoint.getSignature(), e);
        String jsonStr = JsonUtil.toJson(requestInfo);
        log.info("请求接口发生异常 : [{}]", jsonStr);
        if (storage) {
            CompletableFuture.runAsync(() -> logStoreService.saveLog(requestInfo));
        }
        remove();
    }

    /**
     * 构建请求参数日志
     *
     * @param httpServletRequest httpServletRequest
     * @param signature          signature
     * @param args               请求参数
     * @return RequestParamInfo
     */
    private RequestInfo buildRequestParamInfo(HttpServletRequest httpServletRequest, Signature signature, Object[] args) {
        String userId = BaseContextHolder.getUserId(String.class);
        String name = BaseContextHolder.getName();
        return RequestInfo.builder()
                .ip(NetworkUtil.getIpAddress(httpServletRequest))
                .url(httpServletRequest.getRequestURL().toString())
                .httpMethod(httpServletRequest.getMethod())
                .classMethod(String.format("%s.%s", signature.getDeclaringTypeName(),
                        signature.getName()))
                .userId(userId)
                .userName(name)
                .params(getRequestParameterJson(signature, args))
                .build();
    }

    /**
     * 构建请求结果日志
     *
     * @param httpServletRequest httpServletRequest
     * @param signature          signature
     * @param result             返回结果
     * @return RequestInfo
     */
    private RequestInfo buildRequestResultInfo(HttpServletRequest httpServletRequest, Signature signature, Object result) {
        String userId = BaseContextHolder.getUserId(String.class);
        String name = BaseContextHolder.getName();
        return RequestInfo.builder()
                .url(httpServletRequest.getRequestURL().toString())
                .httpMethod(httpServletRequest.getMethod())
                .classMethod(String.format("%s.%s", signature.getDeclaringTypeName(),
                        signature.getName()))
                .userId(userId)
                .userName(name)
                .result(result)
                .build();
    }

    /**
     * 构建请求异常日志
     *
     * @param httpServletRequest httpServletRequest
     * @param signature          signature
     * @param e                  异常
     * @return RequestInfo
     */
    private RequestInfo buildRequestErrorInfo(HttpServletRequest httpServletRequest, Signature signature, Exception e) {
        String userId = BaseContextHolder.getUserId(String.class);
        String name = BaseContextHolder.getName();
        return RequestInfo.builder()
                .url(httpServletRequest.getRequestURL().toString())
                .classMethod(String.format("%s.%s", signature.getDeclaringTypeName(),
                        signature.getName()))
                .userId(userId)
                .userName(name)
                .errorMsg(e.getMessage())
                .error(ExceptionUtil.getMessage(e))
                .throwExceptionClass(e.getClass().getTypeName())
                .build();
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
                if (value instanceof AuthUserInfo) {
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
