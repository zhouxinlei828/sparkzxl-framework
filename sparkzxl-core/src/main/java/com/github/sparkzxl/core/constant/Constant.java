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
            "/favicon.ico",
            "/**/doc.html",
            "/**/swagger-ui.html",
            "/swagger-resources/**",
            "/csrf",
            "/webjars/**",
            "/v2/**",
            "/v3/**",
            "/resources/**",
            "/static/**",
            "/public/**",
            "/classpath:*",
            "/actuator/**"
    );
}
