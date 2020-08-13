package com.sparksys.log.aspect;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Stopwatch;
import com.sparksys.core.utils.RequestContextHolderUtils;
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
import java.util.concurrent.TimeUnit;

/**
 * description: web请求日志切面
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:41:01
 */
@Aspect
@Slf4j
public class WebLogAspect {

    private final ThreadLocal<Stopwatch> stopwatchThreadLocal = new ThreadLocal<>();

    @Pointcut("@within(com.sparksys.log.annotation.WebLog)")
    public void pointCut() {
    }

    /**
     * 前置通知
     *
     * @param joinPoint 切入点
     */
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        Stopwatch stopwatch = get();
        stopwatch.reset();
        stopwatch.start();
        HttpServletRequest request = RequestContextHolderUtils.getRequest();
        JSONObject parameterJson = getRequestParameterJson(joinPoint.getSignature(), joinPoint.getArgs());
        String method = joinPoint.getTarget().getClass().getName().concat(".").concat(joinPoint.getSignature().getName());
        log.info("请求URL：[{}]，请求IP：[{}]", request.getRequestURL(), RequestContextHolderUtils.getIpAddress());
        log.info("请求类型：[{}]，请求方法：[{}]", request.getMethod(), method);
        log.info("请求参数：{}", JSONUtil.toJsonPrettyStr(parameterJson));
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
        JSONObject resultJson = JSONUtil.createObj();
        resultJson.putOpt("result", result);
        log.info("返回结果：{}", JSONUtil.toJsonPrettyStr(resultJson));
        get().stop();
        return result;
    }

    /**
     * 后置通知
     */
    @AfterReturning("pointCut()")
    public void afterReturning() {
        log.info("接口请求耗时：{}毫秒", get().elapsed(TimeUnit.MILLISECONDS));
        remove();
    }

    /**
     * 异常通知，拦截记录异常日志
     */
    @AfterThrowing(pointcut = "pointCut()")
    public void afterThrowing() {
        log.info("接口请求耗时：{}毫秒", get().elapsed(TimeUnit.MILLISECONDS));
        remove();
    }

    public JSONObject getRequestParameterJson(Signature signature, Object[] args) {
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        JSONObject parameterJson = JSONUtil.createObj();
        if (args != null && paramNames != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ServletRequest
                        || args[i] instanceof ServletResponse
                        || args[i] instanceof MultipartFile) {
                    continue;
                }
                parameterJson.putOpt(paramNames[i], args[i]);
            }
        }
        return parameterJson;
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
