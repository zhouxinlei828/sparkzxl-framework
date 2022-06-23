package com.github.sparkzxl.web.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: 拦截器自定义
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.WEB_PREFIX)
public class WebProperties {

    private List<InterceptorProperties> interceptorList = Lists.newArrayList(new InterceptorProperties());

    private boolean transferException = false;

}
