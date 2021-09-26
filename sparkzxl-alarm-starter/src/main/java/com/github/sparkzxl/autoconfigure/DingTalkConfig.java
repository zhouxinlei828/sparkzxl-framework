package com.github.sparkzxl.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 钉钉告警
 *
 * @author zhoux
 */
@Data
@ConfigurationProperties(prefix = "spring.alarm.dingtalk")
public class DingTalkConfig {

    private boolean enabled;

    private String token;

    private String secret;
}
