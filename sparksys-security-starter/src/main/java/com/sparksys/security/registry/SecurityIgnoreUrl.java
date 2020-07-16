package com.sparksys.security.registry;

import com.sparksys.core.utils.ListUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * description: Security 默认放行配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:38:27
 */
@Setter
@Getter
@Slf4j
public class SecurityIgnoreUrl {

    private final List<String> excludePatterns = new ArrayList<>();

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static final List<String> excludeStaticPatterns = Arrays.asList(
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/swagger/**",
            "/**/v2/api-docs",
            "/**/*.js",
            "/**/*.css",
            "/**/*.png",
            "/**/*.ico",
            "/webjars/springfox-swagger-ui/**",
            "/doc.html",
            "/actuator/**"
    );

    public static boolean isIgnore(List<String> list, String currentUri) {
        log.info("忽略地址：{}", ListUtils.listToString(list));
        if (list.isEmpty()) {
            return false;
        }
        return list.stream().anyMatch((url) -> currentUri.startsWith(url) || ANT_PATH_MATCHER.match(url, currentUri));
    }
}
