package com.github.sparkzxl.feign.properties;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: CustomMybatisProperties配置属性类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(
        prefix = "feign"
)
public class FeignProperties {

    private FeignInterceptorProperties interceptor = new FeignInterceptorProperties();

    private FeignSeataProperties seata = new FeignSeataProperties();

    @Data
    public static class FeignSeataProperties {
        private boolean enabled;
    }

    @Data
    public static class FeignInterceptorProperties {
        private List<String> headerList = Lists.newArrayList();
    }

}
