package com.github.sparkzxl.idempotent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "idempotent")
public class IdempotentProperties {

    private boolean enabled;

}
