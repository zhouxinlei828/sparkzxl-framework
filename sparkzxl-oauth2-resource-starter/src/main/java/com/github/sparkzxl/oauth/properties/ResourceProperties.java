package com.github.sparkzxl.oauth.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description:  oauth resource属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.RESOURCE_PREFIX)
public class ResourceProperties {

    /**
     * 需要放行的资源路径
     */
    private String[] ignore;

}
