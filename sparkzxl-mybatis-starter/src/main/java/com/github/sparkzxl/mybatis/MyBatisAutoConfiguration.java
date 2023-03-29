package com.github.sparkzxl.mybatis;

import cn.hutool.core.bean.BeanUtil;
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
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.sparkzxl.core.constant.enums.MultiTenantType;
import com.github.sparkzxl.mybatis.annotation.DataScope;
import com.github.sparkzxl.mybatis.aop.DataScopeAnnotationAdvisor;
import com.github.sparkzxl.mybatis.aop.DataScopeInterceptor;
import com.github.sparkzxl.mybatis.mybatis.hander.MetaDataHandler;
import com.github.sparkzxl.mybatis.mybatis.injector.BaseSqlInjector;
import com.github.sparkzxl.mybatis.plugins.DataScopeInnerInterceptor;
import com.github.sparkzxl.mybatis.plugins.DataScopeLineHandler;
import com.github.sparkzxl.mybatis.plugins.DefaultDataScopeLineHandler;
import com.github.sparkzxl.mybatis.plugins.DynamicSchemaInterceptor;
import com.github.sparkzxl.mybatis.plugins.GlobalLineHandlerInterceptor;
import com.github.sparkzxl.mybatis.plugins.SlowSqlMonitorInterceptor;
import com.github.sparkzxl.mybatis.properties.DataProperties;
import com.github.sparkzxl.mybatis.send.DefaultSendNoticeService;
import com.github.sparkzxl.mybatis.send.SendNoticeService;
import com.github.sparkzxl.mybatis.support.DataBaseExceptionHandler;
import com.google.common.collect.Lists;
import com.p6spy.engine.spy.P6DataSource;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * description: mybatis全局配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({DataProperties.class})
@Import(DataBaseExceptionHandler.class)
@Slf4j
@RequiredArgsConstructor
public class MyBatisAutoConfiguration {

    public static final String DATABASE_PREFIX = "default";
    private final DataProperties dataProperties;

    /**
     * 多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     *
     * @param dataScopeLineHandler 数据权限处理
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataScopeLineHandler dataScopeLineHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        MultiTenantType multiTenantType = dataProperties.getMultiTenantType();
        if (multiTenantType.eq(MultiTenantType.COLUMN)) {
            // 多租户插件
            if (dataProperties.isEnableTenant()) {
                List<String> ignoreTableList = ArrayUtils.isEmpty(dataProperties.getIgnoreTable()) ? Lists.newArrayList() :
                        Arrays.asList(dataProperties.getIgnoreTable());
                dataProperties.getGlobalColumn().forEach(tenant -> interceptor.addInnerInterceptor(
                        new TenantLineInnerInterceptor(new GlobalLineHandlerInterceptor(tenant.getColumn(),
                                tenant.getLoadKey(),
                                ignoreTableList))));
            }
        } else if (multiTenantType.eq(MultiTenantType.SCHEMA)) {
            // 多租户插件
            DynamicSchemaInterceptor dynamicSchemaInterceptor = new DynamicSchemaInterceptor(dataProperties.getDbType(),
                    dataProperties.getTenantDatabasePrefix());
            interceptor.addInnerInterceptor(dynamicSchemaInterceptor);
        }
        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 单页分页条数限制
        paginationInterceptor.setMaxLimit(dataProperties.getMaxLimit());
        // 数据库类型
        paginationInterceptor.setDbType(dataProperties.getDbType());
        // 溢出总页数后是否进行处理
        paginationInterceptor.setOverflow(dataProperties.getOverflow());
        // 生成 countSql 优化掉 join 现在只支持 left join
        paginationInterceptor.setOptimizeJoin(dataProperties.getOptimizeJoin());
        interceptor.addInnerInterceptor(paginationInterceptor);
        //防止全表更新与删除插件
        if (dataProperties.getIsBlockAttack()) {
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        }
        if (dataProperties.isEnableDataScope()) {
            interceptor.addInnerInterceptor(new DataScopeInnerInterceptor(dataScopeLineHandler, dataProperties.getDbType()));
        }
        // sql性能规范插件
        if (dataProperties.getIsIllegalSql()) {
            interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
        }
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DataProperties.DATA_PREFIX, name = "id-type", havingValue = "DEFAULT")
    public UidGenerator getDefaultUidGenerator(DisposableWorkerIdAssigner disposableWorkerIdAssigner) {
        DefaultUidGenerator uidGenerator = new DefaultUidGenerator();
        BeanUtil.copyProperties(dataProperties.getDefaultId(), uidGenerator);
        uidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        return uidGenerator;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DataProperties.DATA_PREFIX, name = "id-type", havingValue = "CACHE")
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
    @ConditionalOnProperty(prefix = DataProperties.DATA_PREFIX, name = "id-type", havingValue = "HU_TOOL", matchIfMissing = true)
    public UidGenerator getHuToolUidGenerator() {
        DataProperties.HuToolId id = dataProperties.getHutoolId();
        return new HuToolUidGenerator(id.getWorkerId(), id.getDataCenterId());
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
    @ConditionalOnProperty(prefix = DataProperties.DATA_PREFIX, name = "p6spy", havingValue = "true")
    public DataSource dataSource(DataSource dataSource) {
        if (dataProperties.getP6spy()) {
            return new P6DataSource(dataSource);
        } else {
            return dataSource;
        }
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
    @ConditionalOnMissingBean(SendNoticeService.class)
    public SendNoticeService sendNoticeService() {
        return new DefaultSendNoticeService();
    }

    @Bean
    public SlowSqlMonitorInterceptor slowSqlMonitorInterceptor(ApplicationContext applicationContext) {
        SlowSqlMonitorInterceptor slowSqlMonitorInterceptor = new SlowSqlMonitorInterceptor();
        slowSqlMonitorInterceptor.setApplicationContext(applicationContext);
        return slowSqlMonitorInterceptor;
    }

    @Bean
    @ConditionalOnProperty(value = "mybatis-plus.data.enable-data-scope", havingValue = "true")
    public DataScopeInterceptor dataScopeInterceptor() {
        return new DataScopeInterceptor();
    }

    @Bean
    @ConditionalOnProperty(value = "mybatis-plus.data.enable-data-scope", havingValue = "true")
    public DataScopeAnnotationAdvisor dataScopeAnnotationAdvisor(DataScopeInterceptor dataScopeInterceptor) {
        return new DataScopeAnnotationAdvisor(dataScopeInterceptor, DataScope.class, Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public DataScopeLineHandler dataScopeLineHandler() {
        return new DefaultDataScopeLineHandler(dataProperties.getDataScopeList());
    }
}
