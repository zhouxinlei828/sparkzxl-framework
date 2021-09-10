package com.github.sparkzxl.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 邮件告警
 *
 * @author zhoux
 * @date 2021-08-21 13:48:29
 */
@Data
@ConfigurationProperties(prefix = "spring.alarm.mail")
public class MailConfig {

    private boolean enabled;

    private String smtpHost;

    private String smtpPort;

    private String to;

    private String from;

    private String username;

    private String password;

    private Boolean ssl = true;

    private Boolean debug = false;
}
