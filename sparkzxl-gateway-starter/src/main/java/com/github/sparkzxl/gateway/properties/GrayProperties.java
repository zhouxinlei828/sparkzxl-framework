package com.github.sparkzxl.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * description: 灰度配置属性
 *
 * @author zhoux
 */
@Slf4j
@ToString
@ConfigurationProperties(prefix = GrayProperties.GRAY_PROPERTIES_PREFIX)
public class GrayProperties {

    public static final String GRAY_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.gray";

    /**
     * Enable Grey Route
     */
    @Getter
    @Setter
    private Boolean enabled = false;
}
