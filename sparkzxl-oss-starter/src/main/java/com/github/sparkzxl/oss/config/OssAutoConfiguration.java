package com.github.sparkzxl.oss.config;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.oss.OssTemplate;
import com.github.sparkzxl.oss.enums.StoreMode;
import com.github.sparkzxl.oss.executor.*;
import com.github.sparkzxl.oss.properties.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * description: oss自动配置
 *
 * @author zhouxinlei
 */
@ConditionalOnProperty(name = "oss.enabled", havingValue = "true")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    private final OssProperties ossProperties;

    public OssAutoConfiguration(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
        StoreMode store = ossProperties.getStore();
        if (ObjectUtils.isEmpty(store)){
            throw new RuntimeException("store cannot be empty.");
        }
        if (store.equals(StoreMode.YAML)) {
            Map<String, OssConfigInfo> provider = ossProperties.getProvider();
            if (MapUtil.isEmpty(provider)) {
                throw new RuntimeException("In yaml mode, provider cannot be empty.");
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(YamlOssConfigProvider.class)
    @ConditionalOnProperty(name = "oss.store", havingValue = "yaml")
    public OssConfigProvider yamlOssConfigProvider() {
        return new YamlOssConfigProvider(ossProperties);
    }

    @Bean
    @ConditionalOnMissingBean(JdbcOssConfigProvider.class)
    @ConditionalOnProperty(name = "oss.store", havingValue = "jdbc")
    public OssConfigProvider ossConfigProvider() {
        return new JdbcOssConfigProvider(ossProperties,(configId)-> Lists.newArrayList());
    }

    @Bean
    @ConditionalOnMissingBean(OssTemplate.class)
    public OssTemplate ossTemplate(List<OssExecutor> executors) {
        OssTemplate ossTemplate = new OssTemplate();
        ossTemplate.setOssProperties(ossProperties);
        ossTemplate.setExecutors(executors);
        return ossTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(MinioExecutor.class)
    public MinioExecutor minioExecutor(OssConfigProvider ossConfigProvider) {
        return new MinioExecutor(ossConfigProvider);
    }

    @Bean
    @ConditionalOnMissingBean(AliYunExecutor.class)
    public AliYunExecutor aliYunExecutor(OssConfigProvider ossConfigProvider) {
        return new AliYunExecutor(ossConfigProvider);
    }

}
