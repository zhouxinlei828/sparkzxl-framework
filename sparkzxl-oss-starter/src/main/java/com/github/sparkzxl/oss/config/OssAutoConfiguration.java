package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.OssTemplate;
import com.github.sparkzxl.oss.enums.RegisterMode;
import com.github.sparkzxl.oss.executor.AliYunExecutor;
import com.github.sparkzxl.oss.executor.MinioExecutor;
import com.github.sparkzxl.oss.executor.OssExecutor;
import com.github.sparkzxl.oss.properties.OssProperties;
import com.github.sparkzxl.oss.provider.JdbcOssConfigProvider;
import com.github.sparkzxl.oss.provider.OssConfigProvider;
import com.github.sparkzxl.oss.provider.YamlOssConfigProvider;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
        RegisterMode registerMode = ossProperties.getRegister();
        if (ObjectUtils.isEmpty(registerMode)) {
            throw new RuntimeException("store cannot be empty.");
        }
        if (registerMode.equals(RegisterMode.YAML)) {
            if (CollectionUtils.isEmpty(ossProperties.getConfigInfoList())) {
                throw new RuntimeException("In yaml mode, configInfoList cannot be empty.");
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(YamlOssConfigProvider.class)
    @ConditionalOnProperty(name = "oss.register", havingValue = "yaml")
    public OssConfigProvider yamlOssConfigProvider() {
        return new YamlOssConfigProvider(ossProperties.getConfigInfoList());
    }

    @Bean
    @ConditionalOnMissingBean(JdbcOssConfigProvider.class)
    @ConditionalOnProperty(name = "oss.register", havingValue = "jdbc")
    public OssConfigProvider ossConfigProvider() {
        return new JdbcOssConfigProvider((clientType) -> Lists.newArrayList());
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
