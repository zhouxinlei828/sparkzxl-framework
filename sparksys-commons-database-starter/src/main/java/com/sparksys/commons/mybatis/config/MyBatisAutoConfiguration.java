package com.sparksys.commons.mybatis.config;

import com.sparksys.commons.mybatis.hander.MetaDataHandler;
import com.sparksys.commons.mybatis.injector.BaseSqlInjector;
import com.sparksys.commons.mybatis.properties.DataProperties;
import org.mybatis.spring.annotation.MapperScan;
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
    @ConditionalOnMissingBean
    public MetaDataHandler metaDateHandler(DataProperties dataProperties) {
        return new MetaDataHandler(dataProperties.getWorkerId(), dataProperties.getDataCenterId());
    }


    @Bean
    public BaseSqlInjector sqlInjector() {
        return new BaseSqlInjector();
    }
}
