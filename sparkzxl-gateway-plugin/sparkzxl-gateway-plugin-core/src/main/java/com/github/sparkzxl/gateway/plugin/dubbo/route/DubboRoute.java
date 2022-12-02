package com.github.sparkzxl.gateway.plugin.dubbo.route;

import lombok.Data;

@Data
public class DubboRoute {

    private final String describe;

    /**
     * 参数类型
     */
    private final String[] parameterTypes;

    private String rpcExt;

}
