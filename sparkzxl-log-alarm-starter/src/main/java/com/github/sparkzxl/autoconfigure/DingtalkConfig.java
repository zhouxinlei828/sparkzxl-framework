package com.github.sparkzxl.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 钉钉告警
 *
 * @author zhoux
 * @date 2021-08-21 13:48:15
 */
@Data
@ConfigurationProperties(prefix = "spring.alarm-log.warn.dingtalk")
public class DingtalkConfig {

    private boolean enabled;

    private String token;

    private String secret;
}
