package com.github.sparkzxl.gateway.plugin.exception.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * description: 异常处理适配器
 *
 * @author zhoux
 */
@Slf4j
public class ExceptionHandlerStrategyAdapter implements ExceptionHandlerStrategy, ApplicationContextAware {


    private final String beanName;
    private final Method method;
    private final Class<? extends Throwable> handleClass;
    private final ResponseStatus statusAnn;
    private ApplicationContext applicationContext;

    public ExceptionHandlerStrategyAdapter(String beanName, Class<?> targetClass, Method method, Class<? extends Throwable> handleClass) {
        this.beanName = beanName;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.handleClass = handleClass;
        Method targetMethod = (!Proxy.isProxyClass(targetClass) ?
                AopUtils.getMostSpecificMethod(method, targetClass) : this.method);
        statusAnn = AnnotatedElementUtils.findMergedAnnotation(targetMethod, ResponseStatus.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Class<? extends Throwable> getHandleClass() {
        return this.handleClass;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        HttpStatus httpStatus = statusAnn == null ? HttpStatus.INTERNAL_SERVER_ERROR : statusAnn.value();
        Object handleResult = handleResultAction(throwable);
        ExceptionHandlerResult exceptionHandlerResult = new ExceptionHandlerResult();
        exceptionHandlerResult.setHttpStatus(httpStatus);
        exceptionHandlerResult.setResponseResult(JSON.toJSONString(handleResult));
        return exceptionHandlerResult;
    }

    /**
     * adapter handle Result
     *
     * @param throwable 异常
     * @return Object
     */
    private Object handleResultAction(Throwable throwable) {
        Object bean = this.applicationContext.getBean(this.beanName);
        ReflectionUtils.makeAccessible(this.method);
        Object[] args = {throwable};
        try {
            return this.method.invoke(bean, throwable);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(getInvocationErrorMessage(bean, ex.getMessage(), args), ex);
        } catch (InvocationTargetException ex) {
            // Throw underlying exception
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            } else {
                String msg = getInvocationErrorMessage(bean, "Failed to invoke exception handler strategy method", args);
                throw new UndeclaredThrowableException(targetException, msg);
            }
        }
    }


    private String getInvocationErrorMessage(Object bean, String message, Object[] resolvedArgs) {
        StringBuilder sb = new StringBuilder(getDetailedErrorMessage(bean, message));
        sb.append("Resolved arguments: \n");
        for (int i = 0; i < resolvedArgs.length; i++) {
            sb.append("[").append(i).append("] ");
            if (resolvedArgs[i] == null) {
                sb.append("[null] \n");
            } else {
                sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
                sb.append("[value=").append(resolvedArgs[i]).append("]\n");
            }
        }
        return sb.toString();
    }

    private String getDetailedErrorMessage(Object bean, String message) {
        return message + "\n" +
                "HandlerMethod details: \n" +
                "Bean [" + bean.getClass().getName() + "]\n" +
                "Method [" + this.method.toGenericString() + "]\n";
    }
}
