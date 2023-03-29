package com.github.sparkzxl.core.constant;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * description: Constant常量类
 *
 * @author zhouxinlei
 * @since 2023-01-03 14:57:58
 */
public class Constant {

    public static List<String> EXCLUDE_STATIC_PATTERNS = Lists.newArrayList(
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/swagger/**",
            "/v2/api-docs",
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
