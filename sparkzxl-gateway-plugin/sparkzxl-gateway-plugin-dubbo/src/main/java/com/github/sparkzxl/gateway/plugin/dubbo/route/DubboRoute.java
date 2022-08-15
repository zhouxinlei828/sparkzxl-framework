package com.github.sparkzxl.gateway.plugin.dubbo.route;

import lombok.Data;

@Data
public class DubboRoute {

    private final String describe;

    /**
     * 调用方法
     */
    private final String method;

    /**
     * 参数类型
     */
    private final String[] parameterTypes;

    private String rpcExt;

}
