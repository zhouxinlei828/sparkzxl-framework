package com.github.sparkzxl.database.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.sparkzxl.database.aspect.InjectionResultAspect;
import com.github.sparkzxl.database.enums.IdTypeEnum;
import com.github.sparkzxl.database.injection.InjectionCore;
import com.github.sparkzxl.database.mybatis.hander.MetaDataHandler;
import com.github.sparkzxl.database.mybatis.hander.RemoteDataTypeHandler;
import com.github.sparkzxl.database.mybatis.injector.BaseSqlInjector;
import com.github.sparkzxl.database.plugins.TenantLineHandlerImpl;
import com.github.sparkzxl.database.properties.CustomMybatisProperties;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * description: mybatis全局配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:20:57
 */
@Configuration
@EnableConfigurationProperties({CustomMybatisProperties.class})
@MapperScan(basePackages = "${mybatis-plus.custom.mapper-scan}")
@Slf4j
public class MyBatisAutoConfiguration {

    private final CustomMybatisProperties customMybatisProperties;

    public MyBatisAutoConfiguration(CustomMybatisProperties customMybatisProperties) {
        this.customMybatisProperties = customMybatisProperties;
        log.info("dataProperties：{}", JSONUtil.toJsonPrettyStr(customMybatisProperties));
    }

    /**
     * 新多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (customMybatisProperties.isEnableTenant()) {
            List<String> ignoreTableList = ArrayUtils.isEmpty(customMybatisProperties.getIgnoreTable()) ? Lists.newArrayList() :
                    Arrays.asList(customMybatisProperties.getIgnoreTable());
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandlerImpl(customMybatisProperties.getTenantIdColumn(),
                    ignoreTableList)));
        }
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(customMybatisProperties.getWorkerId(),
                customMybatisProperties.getDataCenterId());
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaDataHandler metaDataHandler() {
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

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"mybatis-plus.custom.injection.aop-enabled"}, havingValue = "true", matchIfMissing = true)
    public InjectionCore injectionCore(ApplicationContext applicationContext) {
        return new InjectionCore(customMybatisProperties.getInjection(), applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"mybatis-plus.custom.injection.aop-enabled"}, havingValue = "true", matchIfMissing = true)
    public InjectionResultAspect getRemoteAspect(InjectionCore injectionCore) {
        return new InjectionResultAspect(injectionCore);
    }

    /**
     * Mybatis 类型处理器： 处理 RemoteData 类型的字段
     *
     * @return RemoteDataTypeHandler
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"mybatis-plus.custom.injection.aop-enabled"}, havingValue = "true", matchIfMissing = true)
    public RemoteDataTypeHandler remoteDataTypeHandler() {
        return new RemoteDataTypeHandler();
    }

}
