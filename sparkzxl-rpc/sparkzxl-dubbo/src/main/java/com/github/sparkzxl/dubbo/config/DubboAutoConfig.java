package com.github.sparkzxl.dubbo.config;

import com.github.sparkzxl.dubbo.properties.DubboCustomProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * description: dubbo定制化自动装配类
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(DubboCustomProperties.class)
public class DubboAutoConfig {
}
