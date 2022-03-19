package com.github.sparkzxl.gateway.plugin.properties;

import com.github.sparkzxl.gateway.plugin.common.entity.FilterData;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.AntPathMatcher;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * description: gateway plugin properties
 *
 * @author zhouxinlei
 * @date 2022-01-10 09:50:45
 */
@Data
@ConfigurationProperties(prefix = GatewayPluginProperties.PREFIX)
public class GatewayPluginProperties implements InitializingBean, Serializable {

    public static final String PREFIX = "spring.cloud.gateway.plugin";
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private static final long serialVersionUID = -4029281365803511820L;

    private Map<String, FilterData> filter;

    private List<String> ignoring;

    private List<String> staticIgnoring;

    private boolean exceptionJsonHandler;

    @NestedConfigurationProperty
    private LoggingProperties logging;

    public boolean match(String path) {
        boolean match = match(staticIgnoring, path);
        if (match) {
            return true;
        }
        if (CollectionUtils.isEmpty(ignoring)) {
            return false;
        }
        return match(ignoring, path);
    }


    public boolean match(List<String> excludePathList, String path) {
        return excludePathList.stream().anyMatch((url) ->
                ANT_PATH_MATCHER.match(url, path) || path.startsWith(url)
        );
    }

    @Override
    public void afterPropertiesSet() {
        if (CollectionUtils.isEmpty(staticIgnoring)) {
            staticIgnoring = Lists.newArrayList();
        }
        staticIgnoring.addAll(Lists.newArrayList(
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/swagger/**",
                "/**/v2/api-docs",
                "/**/v3/api-docs",
                "/**/*.js",
                "/**/*.css",
                "/**/*.png",
                "/**/*.ico",
                "/favicon.ico",
                "/webjars/**",
                "/doc.html",
                "/actuator/**"));
    }
}
