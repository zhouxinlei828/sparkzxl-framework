package com.github.sparkzxl.gateway.properties;

import com.github.sparkzxl.core.util.SwaggerStaticResource;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * description:  网关resource属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.gateway.plugin.resource")
public class GatewayResourceProperties implements InitializingBean {

    /**
     * 需要放行的资源路径
     */
    private List<String> ignoring;

    private List<String> staticIgnored;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public boolean match(String path) {
        boolean match = match(staticIgnored, path);
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
                path.startsWith(url) || ANT_PATH_MATCHER.match(url, path)
        );
    }

    @Override
    public void afterPropertiesSet() {
        List<String> excludeStaticPatterns = SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS;
        if (CollectionUtils.isEmpty(staticIgnored)) {
            staticIgnored = Lists.newArrayList();
        }
        staticIgnored.addAll(excludeStaticPatterns);
    }
}
