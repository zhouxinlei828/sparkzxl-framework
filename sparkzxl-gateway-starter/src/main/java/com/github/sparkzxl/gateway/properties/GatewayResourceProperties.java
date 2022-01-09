package com.github.sparkzxl.gateway.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.core.util.SwaggerStaticResource;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;

/**
 * description:  网关resource属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.GATEWAY_RESOURCE_PREFIX)
public class GatewayResourceProperties implements InitializingBean {

    /**
     * 需要放行的资源路径
     */
    private Map<String, List<String>> ignoring;

    private List<String> staticIgnored;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public boolean match(String routeId, String path) {
        boolean match = match(staticIgnored, path);
        if (match) {
            return true;
        }
        if (MapUtils.isEmpty(ignoring)) {
            return false;
        }
        List<String> ignoreList = ignoring.get(routeId);
        return match(ignoreList, path);
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
