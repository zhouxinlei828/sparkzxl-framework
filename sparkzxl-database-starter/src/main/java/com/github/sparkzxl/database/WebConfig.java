package com.github.sparkzxl.database;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.github.sparkzxl.database.interceptor.DynamicDataSourceInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
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
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    public static final String DEFAULT_DYNAMIC_DATASOURCE_INTERCEPTOR_NAME = "dynamicDataSourceInterceptor";

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true",
            matchIfMissing = true)
    @Bean(name = DEFAULT_DYNAMIC_DATASOURCE_INTERCEPTOR_NAME)
    public DynamicDataSourceInterceptor dynamicDataSourceInterceptor() {
        return new DynamicDataSourceInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        boolean containsBean = applicationContext.containsBean(DEFAULT_DYNAMIC_DATASOURCE_INTERCEPTOR_NAME);
        if (containsBean) {
            DynamicDataSourceInterceptor dynamicDataSourceInterceptor = applicationContext.getBean(DynamicDataSourceInterceptor.class);
            Optional.of(dynamicDataSourceInterceptor).ifPresent(interceptor -> {
                registry.addInterceptor(interceptor);
                log.info("已加载拦截器：[{}]", ClassUtils.getName(interceptor));
            });
        }
    }
}
