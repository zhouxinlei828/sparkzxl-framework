package com.github.sparkzxl.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 验签属性配置类
 *
 * @author zhouxinlei
 * @date 2021-12-15 19:47
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.gateway.plugin.sign")
public class VerifySignatureProperties {

    /**
     * 请求时间最大 间隔
     */
    private Long timestampIntervalSecond = 12000000L;



}
