package com.github.sparkzxl.database;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.sparkzxl.constant.enums.IdTypeEnum;
import com.github.sparkzxl.database.mybatis.hander.MetaDataHandler;
import com.github.sparkzxl.database.mybatis.injector.BaseSqlInjector;
import com.github.sparkzxl.database.plugins.TenantLineHandlerImpl;
import com.github.sparkzxl.database.properties.CustomMybatisProperties;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * description: mybatis全局配置
 *
 * @author zhouxinlei7
 */
@Configuration
@EnableConfigurationProperties({CustomMybatisProperties.class})
@MapperScan(basePackages = "${mybatis-plus.custom.mapper-scan}")
@Slf4j
public class MyBatisAutoConfiguration {

    private final CustomMybatisProperties customMybatisProperties;

    public MyBatisAutoConfiguration(CustomMybatisProperties customMybatisProperties) {
        this.customMybatisProperties = customMybatisProperties;
    }

    /**
     * 多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 多租户插件
        if (customMybatisProperties.isEnableTenant()) {
            List<String> ignoreTableList = ArrayUtils.isEmpty(customMybatisProperties.getIgnoreTable()) ? Lists.newArrayList() :
                    Arrays.asList(customMybatisProperties.getIgnoreTable());
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandlerImpl(customMybatisProperties.getTenantIdColumn(),
                    ignoreTableList)));
        }
        // 分页插件
        if (customMybatisProperties.isEnablePage()) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(customMybatisProperties.getDbType()));
        }
        return interceptor;
    }

    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(customMybatisProperties.getWorkerId(),
                customMybatisProperties.getDataCenterId());
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaDataHandler() {
        MetaDataHandler metaDataHandler = new MetaDataHandler();
        metaDataHandler.setIdType(customMybatisProperties.getIdType());
        if (IdTypeEnum.SNOWFLAKE_ID.equals(customMybatisProperties.getIdType())) {
            Snowflake snowflake = snowflake();
            metaDataHandler.setSnowflake(snowflake);
        }
        return metaDataHandler;
    }

    @Bean
    public BaseSqlInjector sqlInjector() {
        return new BaseSqlInjector();
    }

}
