package com.github.sparkzxl.grpc.client;

import io.grpc.ClientInterceptor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * description: configuration class that adds a {@link ClientInterceptor} to the global interceptor list.
 *
 * @author zhouxinlei
 * @since 2022-01-25 19:12:04
 */
@Configuration(proxyBeanMethods = false)
public class GlobalClientInterceptorConfiguration {

    /**
     * Creates a new {@link ContextClientGrpcInterceptor} bean and adds it to the global interceptor list. As an alternative you
     * can directly annotate the {@code LogGrpcInterceptor} class and it will automatically be picked up by spring's
     * classpath scanning.
     *
     * @return The newly created bean.
     */
    @GrpcGlobalClientInterceptor
    ClientInterceptor contextClientGrpcInterceptor() {
        return new ContextClientGrpcInterceptor();
    }

}
