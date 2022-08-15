package com.github.sparkzxl.gateway.plugin.common.constant.enums;


import com.github.sparkzxl.gateway.plugin.support.GatewayException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description: RpcTypeEnum
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:23:22
 */
public enum RpcTypeEnum {

    /**
     * Dubbo rpc type enum.
     */
    DUBBO("dubbo", true),


    /**
     * springCloud rpc type enum.
     */
    SPRING_CLOUD("springCloud", true),

    /**
     * motan.
     */
    MOTAN("motan", true),

    /**
     * grpc.
     */
    GRPC("grpc", true);


    private final String name;

    private final Boolean support;

    /**
     * all args constructor.
     *
     * @param name    name
     * @param support support
     */
    RpcTypeEnum(final String name, final Boolean support) {
        this.name = name;
        this.support = support;
    }

    /**
     * acquire operator supports.
     *
     * @return operator support.
     */
    public static List<RpcTypeEnum> acquireSupports() {
        return Arrays.stream(RpcTypeEnum.values())
                .filter(e -> e.support).collect(Collectors.toList());
    }

    /**
     * acquire operator support URI RPC type.
     *
     * @return operator support.
     */
    public static List<RpcTypeEnum> acquireSupportURIs() {
        return Arrays.asList(RpcTypeEnum.GRPC, RpcTypeEnum.SPRING_CLOUD, RpcTypeEnum.DUBBO);
    }

    /**
     * acquire operator support Metadata RPC type.
     *
     * @return operator support.
     */
    public static List<RpcTypeEnum> acquireSupportMetadatas() {
        return Arrays.asList(RpcTypeEnum.DUBBO, RpcTypeEnum.GRPC, RpcTypeEnum.SPRING_CLOUD, RpcTypeEnum.MOTAN);
    }

    /**
     * acquire operator support swagger type.
     *
     * @return operator support.
     */
    public static List<RpcTypeEnum> acquireSupportSwaggers() {
        return Collections.singletonList(RpcTypeEnum.SPRING_CLOUD);
    }

    /**
     * acquireByName.
     *
     * @param name this is rpc type
     * @return RpcTypeEnum rpc type enum
     */
    public static RpcTypeEnum acquireByName(final String name) {
        return Arrays.stream(RpcTypeEnum.values())
                .filter(e -> e.support && e.name.equals(name)).findFirst()
                .orElseThrow(() -> new GatewayException("500", String.format(" this rpc type can not support %s", name)));
    }

    /**
     * get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get support.
     *
     * @return support
     */
    public Boolean getSupport() {
        return support;
    }
}
