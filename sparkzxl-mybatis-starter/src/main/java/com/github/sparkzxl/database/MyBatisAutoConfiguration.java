package com.github.sparkzxl.database;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.buffer.RejectedPutBufferHandler;
import com.baidu.fsg.uid.buffer.RejectedTakeBufferHandler;
import com.baidu.fsg.uid.impl.CachedUidGenerator;
import com.baidu.fsg.uid.impl.DefaultUidGenerator;
import com.baidu.fsg.uid.impl.HuToolUidGenerator;
import com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.constant.enums.MultiTenantType;
import com.github.sparkzxl.database.mybatis.hander.MetaDataHandler;
import com.github.sparkzxl.database.mybatis.injector.BaseSqlInjector;
import com.github.sparkzxl.database.plugins.SchemaInterceptor;
import com.github.sparkzxl.database.plugins.SlowSqlMonitorInterceptor;
import com.github.sparkzxl.database.plugins.TenantLineHandlerInterceptor;
import com.github.sparkzxl.database.properties.CustomMybatisProperties;
import com.github.sparkzxl.database.properties.DataProperties;
import com.github.sparkzxl.database.support.DataBaseExceptionHandler;
import com.google.common.collect.Lists;
import com.p6spy.engine.spy.P6DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
@Import(DataBaseExceptionHandler.class)
@Slf4j
@RequiredArgsConstructor
public class MyBatisAutoConfiguration {

    public static final String DATABASE_PREFIX = "default";
    private final CustomMybatisProperties customMybatisProperties;
    private final DataProperties dataProperties;


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = ConfigurationConstant.DATA_PREFIX, name = "id-type", havingValue = "DEFAULT")
    public UidGenerator getDefaultUidGenerator(DisposableWorkerIdAssigner disposableWorkerIdAssigner) {
        DefaultUidGenerator uidGenerator = new DefaultUidGenerator();
        BeanUtil.copyProperties(dataProperties.getDefaultId(), uidGenerator);
        uidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        return uidGenerator;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = ConfigurationConstant.DATA_PREFIX, name = "id-type", havingValue = "CACHE")
    public UidGenerator getCacheUidGenerator(DisposableWorkerIdAssigner disposableWorkerIdAssigner) {
        CachedUidGenerator uidGenerator = new CachedUidGenerator();
        DataProperties.CacheId cacheId = dataProperties.getCacheId();
        BeanUtil.copyProperties(cacheId, uidGenerator);
        if (cacheId.getRejectedPutBufferHandlerClass() != null) {
            RejectedPutBufferHandler rejectedPutBufferHandler = ReflectUtil.newInstance(cacheId.getRejectedPutBufferHandlerClass());
            uidGenerator.setRejectedPutBufferHandler(rejectedPutBufferHandler);
        }
        if (cacheId.getRejectedTakeBufferHandlerClass() != null) {
            RejectedTakeBufferHandler rejectedTakeBufferHandler = ReflectUtil.newInstance(cacheId.getRejectedTakeBufferHandlerClass());
            uidGenerator.setRejectedTakeBufferHandler(rejectedTakeBufferHandler);
        }
        uidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        return uidGenerator;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = ConfigurationConstant.DATA_PREFIX, name = "id-type", havingValue = "HU_TOOL", matchIfMissing = true)
    public UidGenerator getHuToolUidGenerator() {
        DataProperties.HutoolId id = dataProperties.getHutoolId();
        return new HuToolUidGenerator(id.getWorkerId(), id.getDataCenterId());
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
                interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandlerInterceptor(dataProperties.getTenantIdColumn(),
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
    @ConditionalOnProperty(prefix = ConfigurationConstant.DATA_SOURCE_DYNAMIC_PREFIX, name = "enabled", havingValue = "false")
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
        return new MetaDataHandler();
    }

    @Bean
    public BaseSqlInjector sqlInjector() {
        return new BaseSqlInjector();
    }

    @Bean
    public SlowSqlMonitorInterceptor slowSqlMonitorInterceptor(ApplicationContext applicationContext) {
        SlowSqlMonitorInterceptor slowSqlMonitorInterceptor = new SlowSqlMonitorInterceptor();
        slowSqlMonitorInterceptor.setApplicationContext(applicationContext);
        return slowSqlMonitorInterceptor;
    }

}
