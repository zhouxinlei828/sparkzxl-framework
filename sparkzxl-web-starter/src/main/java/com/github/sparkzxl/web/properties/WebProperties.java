package com.github.sparkzxl.web.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 拦截器自定义
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.WEB_PREFIX)
public class WebProperties {

    private String interceptor = "responseResultInterceptor";

}
