package com.sparksys.commons.cloud.config;

import com.sparksys.commons.cloud.ribbon.DiscoveryEnabledRule;
import com.sparksys.commons.cloud.ribbon.MetadataAwareRule;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * description: 配置ribbon 相关
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:16:47
 */
@Configuration
@AutoConfigureBefore(RibbonClientConfiguration.class)
public class RibbonMetaFilterAutoConfiguration {

    /**
     * 灰度发布 规则
     *
     * @return GrayRule
     */
    @Bean
    @ConditionalOnMissingBean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DiscoveryEnabledRule metadataAwareRule() {
        return new MetadataAwareRule();
    }
}
