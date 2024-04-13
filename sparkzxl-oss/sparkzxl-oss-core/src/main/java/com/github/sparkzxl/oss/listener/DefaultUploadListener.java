package com.github.sparkzxl.oss.listener;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.oss.context.OssClientContextHolder;
import org.aopalliance.intercept.MethodInvocation;

/**
 * description: 默认上传监听类
 *
 * @author zhouxinlei
 * @since 2022-09-27 09:42:10
 */
public class DefaultUploadListener implements UploadListener {

    @Override
    public void onListener(MethodInvocation invocation) {
        OssClientContextHolder.push(RequestLocalContextHolder.getTenantId());
    }

    @Override
    public void afterListener(MethodInvocation invocation) {
        OssClientContextHolder.poll();
    }
}
