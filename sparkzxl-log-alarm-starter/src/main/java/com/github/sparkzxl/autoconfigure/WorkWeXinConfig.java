package com.github.sparkzxl.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 企业微信告警
 *
 * @author zhoux
 * @date 2021-08-21 13:48:40
 */
@Data
@ConfigurationProperties(prefix = "spring.alarm-log.warn.wechat")
public class WorkWeXinConfig {

    private boolean enabled;

    private String to;

    private Integer applicationId;

    private String corpId;

    private String corpSecret;

}
