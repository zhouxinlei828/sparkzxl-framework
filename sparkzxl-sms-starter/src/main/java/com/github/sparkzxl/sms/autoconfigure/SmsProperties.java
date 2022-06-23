package com.github.sparkzxl.sms.autoconfigure;

import com.github.sparkzxl.sms.constant.enums.SmsChannel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 短信配置信息
 *
 * @author zhouxinlei
 * @since 2022-01-03 13:57:04
 */
@Data
@ConfigurationProperties(prefix = SmsProperties.PREFIX)
public class SmsProperties {

    public static final String PREFIX = "sms";
    private boolean enabled;

    private SmsChannel channel = SmsChannel.ALIYUN;

    private String region;

    private String sdkAppId;

    private String accessKeyId;

    private String accessKeySecret;

    private String sign;

    private String endpoint;
}
