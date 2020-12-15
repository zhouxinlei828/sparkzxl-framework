package com.github.sparkzxl.drools.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: drools 配置属性
 *
 * @author: zhouxinlei
 * @date: 2020-12-15 10:49:35
*/
@Data
@ConfigurationProperties(prefix = "drools")
public class DroolsProerties {

    private String rulesPath;

    private int poolSize = 16;

}
