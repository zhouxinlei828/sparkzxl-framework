package com.github.sparkzxl.grpc.core;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.grpc.error.Error;
import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;

/**
 * description: grpc header context holder
 *
 * @author zhouxinlei
 * @date 2022-01-25 10:19:37
 */
public interface GrpcHeaderContextHolder {
    Metadata.Key<String> HEADER_KEY = Metadata.Key.of(BaseContextConstants.REQUEST_THREAD_LOCAL_MAP, Metadata.ASCII_STRING_MARSHALLER);
    Metadata.Key<Error.ErrorInfo> ERROR_INFO_TRAILER_KEY =
            ProtoUtils.keyForProto(Error.ErrorInfo.getDefaultInstance());
}
