package com.github.sparkzxl.gateway.plugin.dubbo.config;

import lombok.Data;

/**
 * description: dubbo参数
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:04:27
 */
@Data
public class DubboParam {

    /**
     * the group.
     */
    private String group;

    /**
     * the version.
     */
    private String version;

    /**
     * the loadbalance.
     */
    private String loadbalance;

    /**
     * the retries.
     */
    private Integer retries;

    /**
     * the timeout.
     */
    private Integer timeout;

    /**
     * the url.
     */
    private String url;

    /**
     * the sent.
     */
    private Boolean sent;

    /**
     * the cluster.
     */
    private String cluster;

}
