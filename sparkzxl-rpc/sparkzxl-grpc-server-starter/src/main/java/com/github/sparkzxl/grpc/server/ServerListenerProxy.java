package com.github.sparkzxl.grpc.server;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import io.grpc.ServerCall;

import java.util.Map;
import java.util.Objects;

/**
 * description: 服务监听代理
 *
 * @author zhouxinlei
 * @date 2022-01-24 16:59:12
 */
public class ServerListenerProxy<ReqT> extends ServerCall.Listener<ReqT> {

    private final Map<String, Object> threadLocalMap;
    private ServerCall.Listener<ReqT> target;


    public ServerListenerProxy(Map<String, Object> threadLocalMap, ServerCall.Listener<ReqT> target) {
        super();
        Objects.requireNonNull(target);
        this.target = target;
        this.threadLocalMap = threadLocalMap;
    }

    @Override
    public void onMessage(ReqT message) {
        target.onMessage(message);
    }

    @Override
    public void onHalfClose() {
        if (MapUtil.isNotEmpty(threadLocalMap)) {
            RequestLocalContextHolder.setLocalMap(this.threadLocalMap);
        }
        target.onHalfClose();
    }

    @Override
    public void onCancel() {
        RequestLocalContextHolder.remove();
        target.onCancel();
    }

    @Override
    public void onComplete() {
        RequestLocalContextHolder.remove();
        target.onComplete();
    }

    @Override
    public void onReady() {
        target.onReady();
    }
}
