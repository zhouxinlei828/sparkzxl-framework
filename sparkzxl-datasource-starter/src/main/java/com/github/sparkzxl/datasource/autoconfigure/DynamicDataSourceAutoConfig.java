package com.github.sparkzxl.datasource.autoconfigure;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.dynamic.datasource.event.DataSourceInitEvent;
import com.baomidou.dynamic.datasource.event.EncDataSourceInitEvent;
import com.baomidou.dynamic.datasource.processor.DsHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.baomidou.dynamic.datasource.processor.jakarta.DsJakartaHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.jakarta.DsJakartaSessionProcessor;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.YmlDynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDatasourceAopProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidDynamicDataSourceConfiguration;
import com.github.sparkzxl.datasource.dynamic.DynamicRoutingDataSource;
import com.github.sparkzxl.datasource.dynamic.aop.DynamicDataSourceAnnotationAdvisor;
import com.github.sparkzxl.datasource.dynamic.aop.DynamicDataSourceAnnotationInterceptor;
import com.github.sparkzxl.datasource.dynamic.aop.DynamicLocalTransactionInterceptor;
import com.github.sparkzxl.datasource.interceptor.DynamicDataSourceInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * description: 动态数据源全局配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({DynamicDataSourceProperties.class, DynamicDataProperties.class})
@Slf4j
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
@Import(value = {DruidDynamicDataSourceConfiguration.class, DynamicDataSourceCreatorAutoConfiguration.class})
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceAutoConfig implements WebMvcConfigurer, InitializingBean {

    public static final String DEFAULT_DYNAMIC_DATASOURCE_INTERCEPTOR_NAME = "dynamicDataSourceInterceptor";
    private DynamicDataProperties dynamicDataProperties;

    private final DynamicDataSourceProperties properties;

    private final List<DynamicDataSourcePropertiesCustomizer> dataSourcePropertiesCustomizers;

    public DynamicDataSourceAutoConfig(
            DynamicDataSourceProperties properties,
            ObjectProvider<List<DynamicDataSourcePropertiesCustomizer>> dataSourcePropertiesCustomizers) {
        log.info("Dynamic data source autoconfiguration is enabled");
        this.properties = properties;
        this.dataSourcePropertiesCustomizers = dataSourcePropertiesCustomizers.getIfAvailable();
    }

    @Autowired
    public void setDynamicDataProperties(DynamicDataProperties dynamicDataProperties) {
        this.dynamicDataProperties = dynamicDataProperties;
    }

    @Bean(name = DEFAULT_DYNAMIC_DATASOURCE_INTERCEPTOR_NAME)
    @ConditionalOnMissingBean
    public DynamicDataSourceInterceptor dynamicDataSourceInterceptor() {
        return new DynamicDataSourceInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (dynamicDataProperties.isEnabled()) {
            DynamicDataSourceInterceptor dynamicDataSourceInterceptor = dynamicDataSourceInterceptor();
            Optional.of(dynamicDataSourceInterceptor).ifPresent(interceptor -> {
                registry.addInterceptor(interceptor);
                log.info("已加载拦截器：[{}]", ClassUtils.getName(interceptor));
            });
        }
    }


    @Bean
    @Order(0)
    public DynamicDataSourceProvider ymlDynamicDataSourceProvider() {
        return new YmlDynamicDataSourceProvider(properties.getDatasource());
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX
            + ".aop", name = "enabled", havingValue = "true", matchIfMissing = true)
    public Advisor dynamicDatasourceAnnotationAdvisor(DsProcessor dsProcessor) {
        DynamicDatasourceAopProperties aopProperties = properties.getAop();
        DynamicDataSourceAnnotationInterceptor interceptor = new DynamicDataSourceAnnotationInterceptor(
                aopProperties.getAllowedPublicOnly(), dsProcessor);
        DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor, DS.class);
        advisor.setOrder(aopProperties.getOrder());
        return advisor;
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "seata", havingValue = "false", matchIfMissing = true)
    public Advisor dynamicTransactionAdvisor() {
        DynamicLocalTransactionInterceptor interceptor = new DynamicLocalTransactionInterceptor();
        return new DynamicDataSourceAnnotationAdvisor(interceptor, DSTransactional.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceInitEvent dataSourceInitEvent() {
        return new EncDataSourceInitEvent();
    }

    @Bean
    @ConditionalOnMissingBean
    public DsProcessor dsProcessor(BeanFactory beanFactory) {
        String version = SpringBootVersion.getVersion();
        DsProcessor headerProcessor;
        DsProcessor sessionProcessor;
        if (version.startsWith("3")) {
            headerProcessor = new DsJakartaHeaderProcessor();
            sessionProcessor = new DsJakartaSessionProcessor();
        } else {
            headerProcessor = new DsHeaderProcessor();
            sessionProcessor = new DsSessionProcessor();
        }
        DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();
        spelExpressionProcessor.setBeanResolver(new BeanFactoryResolver(beanFactory));
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelExpressionProcessor);
        return headerProcessor;
    }

    @Override
    public void afterPropertiesSet() {
        if (!CollectionUtils.isEmpty(dataSourcePropertiesCustomizers)) {
            for (DynamicDataSourcePropertiesCustomizer customizer : dataSourcePropertiesCustomizers) {
                customizer.customize(properties);
            }
        }
    }
}
