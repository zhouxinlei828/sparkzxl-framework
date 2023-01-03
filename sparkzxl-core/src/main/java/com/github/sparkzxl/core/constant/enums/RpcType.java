package com.github.sparkzxl.core.constant.enums;

/**
 * description: RPC类型
 *
 * @author zhouxinlei
 * @since 2022-12-13 13:58:07
 */
public enum RpcType {
    HTTP("http"),
    DUBBO("dubbo"),
    GRPC("grpc"),
    ;
    private final String code;

    RpcType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
