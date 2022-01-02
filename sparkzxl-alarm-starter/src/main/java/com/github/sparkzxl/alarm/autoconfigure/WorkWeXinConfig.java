package com.github.sparkzxl.alarm.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 企业微信告警
 *
 * @author zhoux
 */
@Data
@ConfigurationProperties(prefix = WorkWeXinConfig.PREFIX)
public class WorkWeXinConfig {

    public static final String PREFIX = "spring.alarm.wechat";

    private boolean enabled;

    private String to;

    private Integer applicationId;

    private String corpId;

    private String corpSecret;

}
