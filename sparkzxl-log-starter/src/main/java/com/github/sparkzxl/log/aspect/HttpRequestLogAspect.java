package com.github.sparkzxl.log.aspect;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.entity.AuthUserInfo;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.core.util.RequestContextUtils;
import com.github.sparkzxl.log.annotation.HttpRequestLog;
import com.github.sparkzxl.log.entity.RequestInfoLog;
import com.github.sparkzxl.log.event.HttpRequestLogEvent;
import com.github.sparkzxl.log.utils.LogUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

/**
 * description: web请求日志切面
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class HttpRequestLogAspect {

    public static final int MAX_LENGTH = 65535;
    private static final ThreadLocal<RequestInfoLog> THREAD_LOCAL = new ThreadLocal<>();
    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@within(com.github.sparkzxl.log.annotation.HttpRequestLog)|| @annotation(com.github.sparkzxl.log.annotation.HttpRequestLog)")
    public void pointCut() {

    }

    /**
     * 对Controller下面的方法执行前进行切入，初始化开始时间
     */
    @Before("pointCut()")
    public void beforeMethod(JoinPoint joinPoint) {
        tryCatch((x) -> {
            HttpRequestLog httpRequestLog = LogUtils.getTargetAnnotation(joinPoint);
            HttpServletRequest httpServletRequest = RequestContextUtils.getRequest();
            assert httpRequestLog != null;
            RequestInfoLog requestResultInfo = buildRequestInfoLog(httpServletRequest, joinPoint, httpRequestLog);
            THREAD_LOCAL.set(requestResultInfo);
        });
    }

    /**
     * 后置通知
     */
    @AfterReturning(returning = "ret", pointcut = "pointCut()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch((x) -> {
            HttpRequestLog httpRequestLog = LogUtils.getTargetAnnotation(joinPoint);
            if (check(joinPoint, httpRequestLog)) {
                return;
            }
            RequestInfoLog requestInfoLog = getRequestInfoLog();
            if (httpRequestLog.response() && ObjectUtils.isNotEmpty(ret)) {
                requestInfoLog.setResult(JsonUtils.getJson().toJson(ret));
            }
            publishEvent(requestInfoLog);
        });
    }

    /**
     * 异常通知，拦截记录异常日志
     */
    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Exception e) {
        tryCatch((x) -> {
            HttpRequestLog httpRequestLog = LogUtils.getTargetAnnotation(joinPoint);
            if (check(joinPoint, httpRequestLog)) {
                return;
            }
            RequestInfoLog requestInfoLog = getRequestInfoLog();
            requestInfoLog.setErrorMsg(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));
            requestInfoLog.setThrowExceptionClass(e.getClass().getTypeName());
            publishEvent(requestInfoLog);
        });
    }

    private void publishEvent(RequestInfoLog requestInfoLog) {
        requestInfoLog.setFinishTime(LocalDateTime.now());
        requestInfoLog.setConsumingTime(DateUtils.formatBetween(requestInfoLog.getStartTime(), requestInfoLog.getFinishTime(), BetweenFormatter.Level.MILLISECOND));
        SpringContextUtils.publishEvent(new HttpRequestLogEvent(requestInfoLog));
        remove();
    }

    /**
     * 监测是否需要记录日志
     *
     * @param joinPoint      端点
     * @param httpRequestLog 请求操作日志
     * @return true 表示不需要记录日志
     */
    private boolean check(JoinPoint joinPoint, HttpRequestLog httpRequestLog) {
        if (httpRequestLog == null || !httpRequestLog.enabled()) {
            return true;
        }
        // 读取目标类上的注解
        HttpRequestLog targetClass = joinPoint.getTarget().getClass().getAnnotation(HttpRequestLog.class);
        // 加上 httpRequestLog == null 会导致父类上的方法永远需要记录日志
        return targetClass != null && !targetClass.enabled();
    }

    /**
     * 构建请求结果日志
     *
     * @param request        request
     * @param joinPoint      joinPoint
     * @param httpRequestLog httpRequestLog
     * @return RequestInfo
     */
    private RequestInfoLog buildRequestInfoLog(HttpServletRequest request, JoinPoint joinPoint, HttpRequestLog httpRequestLog) {
        String userId = RequestLocalContextHolder.getUserId(String.class);
        String name = RequestLocalContextHolder.getName();
        Signature signature = joinPoint.getSignature();
        RequestInfoLog requestInfoLog = new RequestInfoLog()
                .setCategory(httpRequestLog.value())
                .setUserId(userId)
                .setUserName(name)
                .setIp(ServletUtil.getClientIP(request))
                .setRequestUrl(URLUtil.getPath(request.getRequestURI()))
                .setHttpMethod(request.getMethod())
                .setClassMethod(String.format("%s.%s", signature.getDeclaringTypeName(), signature.getName()))
                .setStartTime(LocalDateTime.now())
                .setTenantId(RequestLocalContextHolder.getTenant());
        if (httpRequestLog.request()) {
            String requestParameterJson = getRequestParameterJson(joinPoint.getSignature(), joinPoint.getArgs());
            requestInfoLog.setRequestParams(requestParameterJson);
        }
        return requestInfoLog;
    }

    public String getRequestParameterJson(Signature signature, Object[] args) {
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
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
        return JsonUtils.getJson().toJson(parameterMap);
    }

    public void remove() {
        THREAD_LOCAL.remove();
    }

    public RequestInfoLog getRequestInfoLog() {
        RequestInfoLog requestInfoLog = THREAD_LOCAL.get();
        if (ObjectUtils.isEmpty(requestInfoLog)) {
            requestInfoLog = new RequestInfoLog();
            set(requestInfoLog);
        }
        return requestInfoLog;
    }

    public void set(RequestInfoLog requestInfoLog) {
        THREAD_LOCAL.set(requestInfoLog);
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            log.warn("记录日志异常", e);
            THREAD_LOCAL.remove();
        }
    }

}
