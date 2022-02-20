package com.github.sparkzxl.grpc.core;

import com.github.sparkzxl.constant.BaseContextConstants;
import io.grpc.Metadata;

/**
 * description: grpc header context holder
 *
 * @author zhouxinlei
 * @date 2022-01-25 10:19:37
 */
public interface GrpcHeaderContextHolder {
    Metadata.Key<String> HEADER_KEY = Metadata.Key.of(BaseContextConstants.REQUEST_THREAD_LOCAL_MAP, Metadata.ASCII_STRING_MARSHALLER);
}
