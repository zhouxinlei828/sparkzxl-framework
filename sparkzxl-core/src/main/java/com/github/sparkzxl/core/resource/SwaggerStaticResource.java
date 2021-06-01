package com.github.sparkzxl.core.resource;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * description: 静态资源过滤
 *
 * @author zhouxinlei
 */
public class SwaggerStaticResource {

    public static List<String> EXCLUDE_STATIC_PATTERNS = Lists.newArrayList(
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/swagger/**",
            "/**/v2/api-docs",
            "/v3/api-docs",
            "/**/*.js",
            "/**/*.css",
            "/**/*.png",
            "/**/*.ico",
            "/favicon.ico",
            "/webjars/**",
            "/doc.html",
            "/actuator/**"
    );
}
