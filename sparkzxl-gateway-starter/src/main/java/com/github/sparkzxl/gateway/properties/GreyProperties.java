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
 * @date 2021-10-23 17:38:38
 */
@Slf4j
@ToString
@ConfigurationProperties(prefix = GreyProperties.GREY_PROPERTIES_PREFIX)
public class GreyProperties {

    public static final String GREY_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.grey";

    /**
     * Enable Grey Route
     */
    @Getter
    @Setter
    private Boolean enable = false;
}
