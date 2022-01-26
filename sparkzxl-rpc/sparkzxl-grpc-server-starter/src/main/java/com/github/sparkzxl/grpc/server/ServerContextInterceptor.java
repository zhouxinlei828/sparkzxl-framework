package com.github.sparkzxl.grpc.server;

import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.grpc.core.GrpcHeaderContextHolder;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

/**
 * description: grpc 服务端请求拦截器
 *
 * @author zhouxinlei
 * @date 2022-01-25 10:34:10
 */
public class ServerContextInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        return new ServerListenerProxy<>(getThreadLocalMap(metadata), serverCallHandler.startCall(serverCall, metadata));
    }

    /**
     * get rpc threadLocalMap
     *
     * @param metadata 元数据
     * @return Map<String, Object>
     */
    private Map<String, Object> getThreadLocalMap(Metadata metadata) {
        String threadLocalJsonString = metadata.get(GrpcHeaderContextHolder.HEADER_KEY);
        if (ObjectUtils.isEmpty(threadLocalJsonString)) {
            return null;
        }
        return JsonUtil.toMap(threadLocalJsonString);
    }
}
