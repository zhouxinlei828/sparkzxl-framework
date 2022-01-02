package com.github.sparkzxl.alarm.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 钉钉告警
 *
 * @author zhoux
 */
@Data
@ConfigurationProperties(prefix = DingTalkConfig.PREFIX)
public class DingTalkConfig {

    public static final String PREFIX = "spring.alarm.dingtalk";

    private boolean enabled;

    private String token;

    private String secret;
}
