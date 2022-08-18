package com.github.sparkzxl.framework.context;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-08-18 09:43:14
 */
public interface ApplicationContextHolder {

    /**
     * 获取上下文属性
     *
     * @param name 名称
     * @return String
     */
    String getContext(String name);

    /**
     * 获取应用上下文环境
     *
     * @return String
     */
    String getEnvironment();

    /**
     * 获取客户端ip地址
     *
     * @return String
     */
    String getClientIpAddress();

    /**
     * 版本号
     *
     * @return String
     */
    String getVersion();

    /**
     * 链路id
     *
     * @return String
     */
    String traceId();
}
