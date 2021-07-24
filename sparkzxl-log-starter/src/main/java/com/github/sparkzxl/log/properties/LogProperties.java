package com.github.sparkzxl.log.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * description: 日志配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.LOG_PREFIX)
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
    private FileProperties file = new FileProperties();

    @NestedConfigurationProperty
    private KafkaProperties kafka = new KafkaProperties();


}
