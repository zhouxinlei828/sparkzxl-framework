package com.github.sparkzxl.grpc.client;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.grpc.core.GrpcHeaderContextHolder;
import io.grpc.*;

import java.util.Map;

/**
 * description: 客户端请求拦截器
 *
 * @author zhouxinlei
 * @date 2022-01-25 10:39:47
 */
public class ContextClientGrpcInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel channel) {
        Map<String, Object> threadLocalMap = RequestLocalContextHolder.getLocalMap();
        String threadLocalMapJsonString = JsonUtil.toJson(threadLocalMap);
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                if (threadLocalMapJsonString != null) {
                    headers.put(GrpcHeaderContextHolder.HEADER_KEY, threadLocalMapJsonString);
                }
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}