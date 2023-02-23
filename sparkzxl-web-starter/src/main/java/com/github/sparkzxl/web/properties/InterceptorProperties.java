package com.github.sparkzxl.web.properties;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * description: 拦截器配置
 *
 * @author zhouxinlei
 * @since 2022-05-07 09:07:03
 */
@Data
public class InterceptorProperties {

    private String name;

    private List<String> includePatterns = Lists.newArrayList("/**");

    private List<String> excludePatterns = Lists.newArrayList();
}
