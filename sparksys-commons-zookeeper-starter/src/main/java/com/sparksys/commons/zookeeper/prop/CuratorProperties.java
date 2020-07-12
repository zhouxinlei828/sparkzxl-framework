package com.sparksys.commons.zookeeper.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description: zookeeper属性配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:45:52
 */
@Data
@Component
@ConfigurationProperties(prefix = "zookeeper.curator")
public class CuratorProperties {

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 重试间隔时间
     */
    private Integer elapsedTimeMs;

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
