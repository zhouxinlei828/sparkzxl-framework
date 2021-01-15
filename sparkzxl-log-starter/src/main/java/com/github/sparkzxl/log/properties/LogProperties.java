package com.github.sparkzxl.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * description: 日志配置类
 *
 * @author: zhouxinlei
 * @date: 2020-12-09 11:40:24
*/
@Data
@ConfigurationProperties(prefix = "logging")
public class LogProperties {

    /**
     * 是否开启控制台输出
     */
    private boolean enableConsole = true;

    @NestedConfigurationProperty
    private FileProperties file;

    @NestedConfigurationProperty
    private KafkaProperties kafka;


}
