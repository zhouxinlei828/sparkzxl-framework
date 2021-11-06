package com.github.sparkzxl.log.aspect;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.utils.NetworkUtil;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import com.github.sparkzxl.log.entity.RequestInfoLog;
import com.github.sparkzxl.log.event.HttpRequestLogEvent;
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description: web请求日志切面
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class HttpRequestLogAspect {

    private final ThreadLocal<Stopwatch> stopwatchThreadLocal = new ThreadLocal<>();

    @Pointcut("@within(com.github.sparkzxl.log.annotation.HttpRequestLog)|| @annotation(com.github.sparkzxl.log.annotation.HttpRequestLog)")
    public void pointCut() {

    }

    /**
     * 对Controller下面的方法执行前进行切入，初始化开始时间
     */
    @Before("pointCut()")
    public void beforeMethod() {
        Stopwatch stopwatch = get();
        if (stopwatch.isRunning()) {
            stopwatch.reset().start();
        } else {
            stopwatch.start();
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
        RequestInfoLog requestResultInfo = buildRequestResultInfo(httpServletRequest, proceedingJoinPoint, result);
        SpringContextUtils.publishEvent(new HttpRequestLogEvent(requestResultInfo));
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
        RequestInfoLog requestInfoLog = buildRequestErrorInfo(httpServletRequest, joinPoint, e);
        log.info("请求接口发生异常 : [{}]", requestInfoLog.getErrorMsg());
        SpringContextUtils.publishEvent(new HttpRequestLogEvent(requestInfoLog));
        remove();
    }

    /**
     * 构建基本请求日志
     *
     * @param httpServletRequest httpServletRequest
     * @param joinPoint          joinPoint
     * @return RequestInfoLog
     */
    private RequestInfoLog buildBaseRequestInfo(HttpServletRequest httpServletRequest, JoinPoint joinPoint) {
        String userId = RequestLocalContextHolder.getUserId(String.class);
        String name = RequestLocalContextHolder.getName();
        Signature signature = joinPoint.getSignature();
        String category = LockKeyGenerator.getLockKey(joinPoint);
        Map<String, Object> requestParameterJson = getRequestParameterJson(signature, joinPoint.getArgs());
        String parameterJson = JsonUtil.toJson(requestParameterJson);
        return new RequestInfoLog()
                .setCategory(category)
                .setUserId(userId)
                .setUserName(name)
                .setIp(NetworkUtil.getIpAddress(httpServletRequest))
                .setRequestUrl(httpServletRequest.getRequestURL().toString())
                .setClassMethod(String.format("%s.%s", signature.getDeclaringTypeName(),
                        signature.getName()))
                .setRequestParams(parameterJson)
                .setCreateTime(LocalDateTime.now())
                .setTenantId(RequestLocalContextHolder.getTenant());
    }

    /**
     * 构建请求结果日志
     *
     * @param httpServletRequest  httpServletRequest
     * @param proceedingJoinPoint proceedingJoinPoint
     * @param result              返回结果
     * @return RequestInfo
     */
    private RequestInfoLog buildRequestResultInfo(HttpServletRequest httpServletRequest, ProceedingJoinPoint proceedingJoinPoint, Object result) {
        RequestInfoLog requestInfoLog = buildBaseRequestInfo(httpServletRequest, proceedingJoinPoint);
        if (ObjectUtils.isNotEmpty(result)) {
            requestInfoLog.setResponseResult(JsonUtil.toJson(result));
        }
        return requestInfoLog;
    }

    /**
     * 构建请求异常日志
     *
     * @param httpServletRequest httpServletRequest
     * @param joinPoint          signature
     * @param e                  异常
     * @return RequestInfoLog
     */
    private RequestInfoLog buildRequestErrorInfo(HttpServletRequest httpServletRequest, JoinPoint joinPoint, Exception e) {
        RequestInfoLog requestInfoLog = buildBaseRequestInfo(httpServletRequest, joinPoint);
        requestInfoLog.setErrorMsg(e.getMessage());
        requestInfoLog.setThrowExceptionClass(e.getClass().getTypeName());
        return requestInfoLog;
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
