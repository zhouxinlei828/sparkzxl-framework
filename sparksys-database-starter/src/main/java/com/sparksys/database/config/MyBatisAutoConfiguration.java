package com.sparksys.database.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.sparksys.database.mybatis.hander.MetaDataHandler;
import com.sparksys.database.mybatis.injector.BaseSqlInjector;
import com.sparksys.database.properties.DataProperties;
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
    public Snowflake snowflake(DataProperties dataProperties) {
        return IdUtil.createSnowflake(dataProperties.getWorkerId(), dataProperties.getDataCenterId());
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaDataHandler metaDateHandler(Snowflake snowflake) {
        return new MetaDataHandler(snowflake);
    }


    @Bean
    public BaseSqlInjector sqlInjector() {
        return new BaseSqlInjector();
    }
}
