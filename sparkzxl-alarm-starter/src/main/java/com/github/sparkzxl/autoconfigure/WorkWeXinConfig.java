package com.github.sparkzxl.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 企业微信告警
 *
 * @author zhoux
 */
@Data
@ConfigurationProperties(prefix = "spring.alarm.wechat")
public class WorkWeXinConfig {

    private boolean enabled;

    private String to;

    private Integer applicationId;

    private String corpId;

    private String corpSecret;

}
