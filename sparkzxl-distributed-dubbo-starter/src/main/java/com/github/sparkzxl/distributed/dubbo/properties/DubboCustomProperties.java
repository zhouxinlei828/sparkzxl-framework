package com.github.sparkzxl.distributed.dubbo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * description: 自定义配置
 *
 * @author zhouxinlei
 * @since 2022-08-06 14:23:34
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "dubbo.custom")
public class DubboCustomProperties {

    private boolean requestLog;

    private LogLevel level;

}
