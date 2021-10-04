package com.github.sparkzxl.gateway.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description:  oauth resource属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.GATEWAY_RESOURCE_PREFIX)
public class ResourceProperties {

    /**
     * 需要放行的资源路径
     */
    private String[] ignore;

}
