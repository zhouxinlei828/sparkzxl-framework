package com.github.sparkzxl.oss.aop;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.oss.annotation.OSSUpload;
import com.github.sparkzxl.oss.listener.UploadListener;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.Objects;

/**
 * description: OSS Upload Interceptor
 *
 * @author zhouxinlei
 * @since 2022-09-27 09:28:55
 */
public class OSSUploadInterceptor implements MethodInterceptor {

    @Nullable
    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
        //fix 使用其他aop组件时,aop切了两次.
        Class<?> cls = AopProxyUtils.ultimateTargetClass(Objects.requireNonNull(invocation.getThis()));
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        OSSUpload annotation = invocation.getMethod().getAnnotation(OSSUpload.class);
        if (!annotation.enabled()) {
            throw new IllegalStateException("Method " + invocation.getMethod() + " is not supported by this OSSUpload");
        }
        Class<? extends UploadListener> listener = annotation.listener();
        UploadListener uploadListener = SpringContextUtils.getBean(listener);
        try {
            uploadListener.onListener(invocation);
            return invocation.proceed();
        } finally {
            uploadListener.afterListener(invocation);
        }
    }
}
