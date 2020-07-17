package com.sparksys.zookeeper.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: zookeeper属性配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:45:52
 */
@Data
@ConfigurationProperties(prefix = "sparksys.zookeeper.curator")
public class CuratorProperties {

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 超时时间(毫秒)，默认1000
     */
    private Integer timeout = 1000;

    /**
     * zookeeper 地址
     */
    private String url;

    /**
     * session超时时间
     */
    private Integer sessionTimeoutMs;

    /**
     * 连接超时时间
     */
    private Integer connectionTimeoutMs;
}
