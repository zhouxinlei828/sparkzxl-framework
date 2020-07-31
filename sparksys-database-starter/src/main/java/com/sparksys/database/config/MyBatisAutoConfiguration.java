package com.sparksys.database.config;

import com.sparksys.core.cache.CacheTemplate;
import com.sparksys.database.context.BaseContextHandler;
import com.sparksys.database.mybatis.hander.MetaDataHandler;
import com.sparksys.database.mybatis.injector.BaseSqlInjector;
import com.sparksys.database.properties.DataProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: mybatis全局配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:20:57
 */
@Configuration
@EnableConfigurationProperties(DataProperties.class)
@MapperScan("${mybatis-plus.mapperScan}")
public class MyBatisAutoConfiguration {

    @Bean
    @ConditionalOnBean(CacheTemplate.class)
    public BaseContextHandler baseContextHandler(CacheTemplate cacheRepository) {
        BaseContextHandler baseContextHandler = new BaseContextHandler();
        baseContextHandler.setCacheRepository(cacheRepository);
        return baseContextHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaDataHandler metaDateHandler(DataProperties dataProperties) {
        return new MetaDataHandler(dataProperties.getWorkerId(), dataProperties.getDataCenterId());
    }


    @Bean
    public BaseSqlInjector sqlInjector() {
        return new BaseSqlInjector();
    }
}
