package com.github.sparkzxl.drools.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: drools 配置属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.DROOLS_PREFIX)
public class DroolsProperties {

    private String rulesPath;

    private int poolSize = 16;

}
