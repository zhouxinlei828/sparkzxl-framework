package com.github.sparkzxl.gateway.plugin.properties;

import com.github.sparkzxl.gateway.plugin.common.constant.enums.RuleEnum;
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
@Getter
@Setter
@ConfigurationProperties(prefix = ReactiveLoadBalancerProperties.GRAY_PROPERTIES_PREFIX)
public class ReactiveLoadBalancerProperties {

    public static final String GRAY_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.loadbalancer";

    /**
     * Enable Grey Route
     */
    private Boolean enabled = false;

    private String rule = RuleEnum.RANDOM.getName();
}
