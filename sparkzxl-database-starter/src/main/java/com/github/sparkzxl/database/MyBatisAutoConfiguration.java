package com.github.sparkzxl.database;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.sparkzxl.constant.enums.IdTypeEnum;
import com.github.sparkzxl.constant.enums.MultiTenantType;
import com.github.sparkzxl.database.mybatis.hander.MetaDataHandler;
import com.github.sparkzxl.database.mybatis.injector.BaseSqlInjector;
import com.github.sparkzxl.database.plugins.SchemaInterceptor;
import com.github.sparkzxl.database.plugins.TenantLineHandlerImpl;
import com.github.sparkzxl.database.properties.CustomMybatisProperties;
import com.github.sparkzxl.database.properties.DataProperties;
import com.github.sparkzxl.database.support.DataSourceExceptionHandler;
import com.google.common.collect.Lists;
import com.p6spy.engine.spy.P6DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * description: mybatis全局配置
 *
 * @author zhouxinlei7
 */
@Configuration
@EnableConfigurationProperties({CustomMybatisProperties.class, DataProperties.class})
@Import(DataSourceExceptionHandler.class)
@MapperScan(basePackages = "${mybatis-plus.custom.mapper-scan}")
@Slf4j
public class MyBatisAutoConfiguration {

    public static final String DATABASE_PREFIX = "default";
    private final CustomMybatisProperties customMybatisProperties;
    private final DataProperties dataProperties;

    public MyBatisAutoConfiguration(CustomMybatisProperties customMybatisProperties, DataProperties dataProperties) {
        this.customMybatisProperties = customMybatisProperties;
        this.dataProperties = dataProperties;
    }

    /**
     * 多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        MultiTenantType multiTenantType = dataProperties.getMultiTenantType();
        if (multiTenantType.eq(MultiTenantType.COLUMN)) {
            // 多租户插件
            if (dataProperties.isEnableTenant()) {
                List<String> ignoreTableList = ArrayUtils.isEmpty(dataProperties.getIgnoreTable()) ? Lists.newArrayList() :
                        Arrays.asList(dataProperties.getIgnoreTable());
                interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandlerImpl(dataProperties.getTenantIdColumn(),
                        ignoreTableList)));
            }
        } else if (multiTenantType.eq(MultiTenantType.SCHEMA)) {
            // 多租户插件
            SchemaInterceptor schemaInterceptor = new SchemaInterceptor(dataProperties.getTenantDatabasePrefix());
            interceptor.addInnerInterceptor(schemaInterceptor);
        }
        // 分页插件
        if (customMybatisProperties.isEnablePage()) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(customMybatisProperties.getDbType()));
        }

        // sql性能规范插件
        if (dataProperties.getIsIllegalSql()) {
            interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
        }

        return interceptor;
    }

    /**
     * 数据源事务管理器
     *
     * @return 数据源事务管理器
     */
    @Primary
    @Bean(name = DATABASE_PREFIX + "TransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = DATABASE_PREFIX + "DataSource")
    @DependsOn("dataSource")
    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "false")
    public DataSource dataSource(DataSource dataSource) {
        if (dataProperties.getP6spy()) {
            return new P6DataSource(dataSource);
        } else {
            return dataSource;
        }
    }

    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(dataProperties.getWorkerId(),
                dataProperties.getDataCenterId());
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaDataHandler() {
        MetaDataHandler metaDataHandler = new MetaDataHandler();
        metaDataHandler.setIdType(dataProperties.getIdType());
        if (IdTypeEnum.SNOWFLAKE_ID.equals(dataProperties.getIdType())) {
            metaDataHandler.setSnowflake(snowflake());
        }
        return metaDataHandler;
    }

    @Bean
    public BaseSqlInjector sqlInjector() {
        return new BaseSqlInjector();
    }

}
