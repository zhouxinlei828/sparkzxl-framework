package com.github.sparkzxl.web.properties;

import static com.github.sparkzxl.web.properties.WebProperties.WEB_PREFIX;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 拦截器自定义
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = WEB_PREFIX)
public class WebProperties {

    public static final String WEB_PREFIX = "spring.web";

    private List<InterceptorProperties> interceptorConfigList = Lists.newArrayList();


}
