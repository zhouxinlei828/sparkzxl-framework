package com.github.sparkzxl.mybatis.aop;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.mybatis.annotation.DataScope;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * description: 多列数据权限aop处理器
 *
 * @author zhouxinlei
 * @since 2022-05-27 12:30:03
 */
@Slf4j
public class DataScopeInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        //fix 使用其他aop组件时,aop切了两次.
        Class<?> cls = AopProxyUtils.ultimateTargetClass(Objects.requireNonNull(invocation.getThis()));
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        Method method = invocation.getMethod();
        DataScope annotation = method.getAnnotation(DataScope.class);
        RequestLocalContextHolder.set(BaseContextConstants.MULTI_DATA_SCOPE, annotation);
        return invocation.proceed();
    }
}
