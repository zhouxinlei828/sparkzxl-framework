package com.github.sparkzxl.data.nacos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-08-24 15:14:41
 */
@Data
@ConfigurationProperties(prefix = "application.sync.nacos")
public class NacosConfig {

    private String url;

    private String namespace;

    private String username;

    private String password;

    private NacosACMConfig acm;

}
