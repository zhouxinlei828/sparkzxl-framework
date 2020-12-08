package com.github.sparkzxl.distributed.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sparkzxl.seata")
public class CustomSeataProperties {

    private boolean enable;

}
