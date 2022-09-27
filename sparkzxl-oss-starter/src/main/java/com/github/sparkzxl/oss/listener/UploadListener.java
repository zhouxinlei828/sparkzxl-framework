package com.github.sparkzxl.oss.listener;

import org.aopalliance.intercept.MethodInvocation;

/**
 * description: 上传监听类
 *
 * @author zhouxinlei
 * @since 2022-09-27 09:19:38
 */
public interface UploadListener {

    /**
     * 监听
     *
     * @param invocation 方法调用
     */
    void onListener(MethodInvocation invocation);

    /**
     * 后置监听
     *
     * @param invocation 方法调用
     */
    void afterListener(MethodInvocation invocation);
}
