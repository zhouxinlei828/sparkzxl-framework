package com.github.sparkzxl.gateway.plugin.common.entity;

import lombok.Data;

/**
 * description: 路由元数据
 *
 * @author zhouxinlei
 * @since 2022-08-12 14:25:11
 */
@Data
public class MetaData {

    private String id;

    private String appName;

    private String contextPath;

    private String path;

    private String rpcType;

    private String serviceName;

    private String methodName;

    private String parameterTypes;

    private String rpcExt;

    private Boolean enabled;

}
