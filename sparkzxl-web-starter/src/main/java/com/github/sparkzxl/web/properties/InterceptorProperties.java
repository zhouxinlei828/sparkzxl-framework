package com.github.sparkzxl.web.properties;

import com.github.sparkzxl.web.interceptor.HeaderThreadLocalInterceptor;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * description: 拦截器配置
 *
 * @author zhouxinlei
 * @since 2022-05-07 09:07:03
 */
@Data
public class InterceptorProperties {

    private Class<? extends HandlerInterceptor> interceptor = HeaderThreadLocalInterceptor.class;

    private List<String> includePatterns = Lists.newArrayList("/**");

    private List<String> excludePatterns = Lists.newArrayList();
}
