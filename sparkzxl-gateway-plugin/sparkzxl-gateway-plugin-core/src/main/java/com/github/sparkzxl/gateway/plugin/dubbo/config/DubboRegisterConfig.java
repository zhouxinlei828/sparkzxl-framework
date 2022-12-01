package com.github.sparkzxl.gateway.plugin.dubbo.config;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * description: dubbo 注册配置
 *
 * @author zhouxinlei
 * @since 2022-08-12 14:58:36
 */
@Data
public class DubboRegisterConfig implements Serializable {

    private String address;

    /**
     * Username to login register center
     */
    private String username;

    /**
     * Password to login register center
     */
    private String password;
    /**
     * Default port for register center
     */
    private Integer port;

    private String group;

    private String protocol;

    /**
     * Consumer thread pool type: cached, fixed, limit, eager
     */
    private String threadPool;

    /**
     * Consumer threadPool core thread size
     */
    private Integer coreThreads;

    /**
     * Consumer threadPool thread size
     */
    private Integer threads;

    private Integer queues;

    private Map<String, String> parameters;

}
