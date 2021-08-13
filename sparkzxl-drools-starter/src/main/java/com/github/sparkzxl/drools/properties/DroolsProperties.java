package com.github.sparkzxl.drools.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: drools 配置属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "drools")
public class DroolsProperties {

    private String rulesPath = "rules";

    private int poolSize = 16;

}
