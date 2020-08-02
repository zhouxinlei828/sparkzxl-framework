package com.sparksys.core.resource;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * description: 静态资源过滤
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:38:27
 */
@Setter
@Getter
@Slf4j
public class SwaggerStaticResource {

    public static final List<String> EXCLUDE_STATIC_PATTERNS = Arrays.asList(
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/swagger/**",
            "/**/v2/api-docs",
            "/**/*.js",
            "/**/*.css",
            "/**/*.png",
            "/**/*.ico",
            "/webjars/**",
            "/doc.html",
            "/actuator/**"
    );
}
