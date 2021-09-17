package com.github.sparkzxl.datasource.autoconfigure;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.datasource.interceptor.DynamicDataSourceInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

/**
 * description: WebConfig全局配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(DynamicDataProperties.class)
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    public static final String DEFAULT_DYNAMIC_DATASOURCE_INTERCEPTOR_NAME = "dynamicDataSourceInterceptor";


    private DynamicDataProperties dynamicDataProperties;

    @Autowired
    public void setDynamicDataProperties(DynamicDataProperties dynamicDataProperties) {
        this.dynamicDataProperties = dynamicDataProperties;
    }

    @ConditionalOnProperty(prefix = ConfigurationConstant.DATA_SOURCE_DYNAMIC_PREFIX, name = "enabled", havingValue = "true",
            matchIfMissing = true)
    @Bean(name = DEFAULT_DYNAMIC_DATASOURCE_INTERCEPTOR_NAME)
    public DynamicDataSourceInterceptor dynamicDataSourceInterceptor() {
        return new DynamicDataSourceInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (dynamicDataProperties.isEnabled()) {
            DynamicDataSourceInterceptor dynamicDataSourceInterceptor = dynamicDataSourceInterceptor();
            Optional.of(dynamicDataSourceInterceptor).ifPresent(interceptor -> {
                registry.addInterceptor(interceptor);
                log.info("已加载拦截器：[{}]", ClassUtils.getName(interceptor));
            });
        }
    }
}
