package com.github.sparkzxl.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * description: 日志配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "logging")
public class LogProperties {

    /**
     * 是否开启控制台输出
     */
    private boolean enableConsole = true;

    /**
     * 是否开启日志存储
     */
    private boolean storage = false;

    @NestedConfigurationProperty
    private FileProperties file;

    @NestedConfigurationProperty
    private KafkaProperties kafka;


}
