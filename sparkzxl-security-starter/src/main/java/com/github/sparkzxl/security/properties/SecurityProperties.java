package com.github.sparkzxl.security.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: security 自动装配属性配置
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.SECURITY_PREFIX)
public class SecurityProperties {

    private List<String> ignore;

    private boolean builtInPermissions;

}
