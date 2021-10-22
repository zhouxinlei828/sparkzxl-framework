package com.github.sparkzxl.distributed.cloud.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 负载均衡配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(
        prefix = ConfigurationConstant.RIBBON_PREFIX
)
public class LoadBalancerRuleProperties {

    private boolean enabled = true;

}
