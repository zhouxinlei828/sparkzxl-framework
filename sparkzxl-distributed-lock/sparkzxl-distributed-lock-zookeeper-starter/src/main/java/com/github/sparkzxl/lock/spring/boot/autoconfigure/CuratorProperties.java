package com.github.sparkzxl.lock.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: zookeeper属性配置
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "spring.coordinate.zookeeper")
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
    private String zkServers;

    /**
     * session超时时间
     */
    private Integer sessionTimeoutMs;

    /**
     * 连接超时时间
     */
    private Integer connectionTimeoutMs;
}
