package com.github.sparkzxl.monitor;

/**
 * description: 追踪执行器接口类
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:49:04
 */
public interface TracerExecutor {

    /**
     * 获取链路id
     *
     * @return String
     */
    String getTraceId();

    /**
     * 链路类型名称
     *
     * @return String
     */
    String name();

}
