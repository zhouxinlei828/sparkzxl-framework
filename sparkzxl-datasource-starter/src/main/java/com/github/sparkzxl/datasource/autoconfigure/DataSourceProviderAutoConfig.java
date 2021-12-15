package com.github.sparkzxl.datasource.autoconfigure;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.github.sparkzxl.datasource.loadbalancer.DataSourceLoadBalancer;
import com.github.sparkzxl.datasource.loadbalancer.RandomDataSourceLoadBalancer;
import com.github.sparkzxl.datasource.provider.DataSourceProvider;
import com.github.sparkzxl.datasource.provider.YamlDataSourceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Configuration
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackageClasses = DataSourceProvider.class)
public class DataSourceProviderAutoConfig {


    @Bean
    @ConditionalOnMissingBean
    public DataSourceLoadBalancer dataSourceLoadBalancer() {
        return new RandomDataSourceLoadBalancer();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceProvider dataSourceProvider() {
        return new YamlDataSourceProvider();
    }
}
