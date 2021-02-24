package com.github.sparkzxl.open.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: open 自动装配属性配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 16:24:55
 */
@Data
@ConfigurationProperties(prefix = "open")
public class OpenProperties {

    private String appId;

}
