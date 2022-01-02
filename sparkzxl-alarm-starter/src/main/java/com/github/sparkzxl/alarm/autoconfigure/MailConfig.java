package com.github.sparkzxl.alarm.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 邮件告警
 *
 * @author zhoux
 */
@Data
@ConfigurationProperties(prefix = MailConfig.PREFIX)
public class MailConfig {

    public static final String PREFIX = "spring.alarm.mail";

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
