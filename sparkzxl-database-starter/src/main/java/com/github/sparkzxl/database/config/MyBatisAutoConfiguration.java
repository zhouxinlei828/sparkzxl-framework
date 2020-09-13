package com.github.sparkzxl.database.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.database.mybatis.hander.MetaDataHandler;
import com.github.sparkzxl.database.mybatis.injector.BaseSqlInjector;
import com.github.sparkzxl.database.properties.DataProperties;
import lombok.extern.slf4j.Slf4j;
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
@EnableConfigurationProperties({DataProperties.class})
@MapperScan(basePackages = "${mybatis-plus.mapper-scan}")
@Slf4j
public class MyBatisAutoConfiguration {

    private final DataProperties dataProperties;

    public MyBatisAutoConfiguration(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
        log.info("dataProperties：{}", JSONUtil.toJsonPrettyStr(dataProperties));
    }

    @Bean
    public Snowflake snowflake() {
        return IdUtil.createSnowflake(dataProperties.getWorkerId(),
                dataProperties.getDataCenterId());
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaDataHandler metaDataHandler(Snowflake snowflake) {
        return new MetaDataHandler(snowflake);
    }

    @Bean
    public BaseSqlInjector sqlInjector() {
        return new BaseSqlInjector();
    }
}
