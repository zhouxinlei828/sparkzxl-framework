package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.OssTemplate;
import com.github.sparkzxl.oss.executor.AmazonS3Executor;
import com.github.sparkzxl.oss.executor.OssExecutor;
import com.github.sparkzxl.oss.properties.OssProperties;
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

    @Bean
    @ConditionalOnMissingBean(OssTemplate.class)
    public OssTemplate ossTemplate(OssProperties ossProperties, List<OssExecutor> executorList) {
        OssTemplate ossTemplate = new OssTemplate();
        ossTemplate.setOssProperties(ossProperties);
        ossTemplate.setExecutors(executorList);
        return ossTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(AmazonS3Executor.class)
    public AmazonS3Executor amazonS3Executor() {
        return new AmazonS3Executor(() -> null);
    }

}
