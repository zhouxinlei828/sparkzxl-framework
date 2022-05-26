package com.github.sparkzxl.grpc.server;

import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * description: configuration class that adds a {@link ServerInterceptor} to the global interceptor list.
 *
 * @author zhouxinlei
 * @since 2022-01-25 16:44:56
 */
@Configuration(proxyBeanMethods = false)
public class GlobalServerInterceptorConfiguration {

    /**
     * Creates a new {@link ServerContextInterceptor} bean and adds it to the global interceptor list. As an alternative you
     * can directly annotate the {@code LogGrpcInterceptor} class and it will automatically be picked up by spring's
     * classpath scanning.
     *
     * @return The newly created bean.
     */
    @GrpcGlobalServerInterceptor
    ServerInterceptor serverContextInterceptor() {
        return new ServerContextInterceptor();
    }

}
