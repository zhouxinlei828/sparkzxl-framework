package com.github.sparkzxl.patterns.config;

import com.github.sparkzxl.patterns.pipeline.ChannelPipeline;
import com.github.sparkzxl.patterns.pipeline.SupperPipeline;
import com.github.sparkzxl.patterns.strategy.BusinessHandler;
import com.github.sparkzxl.patterns.strategy.BusinessStrategyContext;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 设计模式自动注入配置
 *
 * @author zhouxinlei
 */
@Configuration
public class PatternsAutoConfiguration {

    @Bean
    public BusinessStrategyContext businessStrategyContext(@Autowired(required = false) List<BusinessHandler> businessHandlers) {
        BusinessStrategyContext businessStrategyContext = new BusinessStrategyContext();
        if (CollectionUtils.isNotEmpty(businessHandlers)) {
            businessHandlers.forEach(businessStrategyContext::add);
        }
        return businessStrategyContext;
    }

    @Bean
    public ChannelPipeline channelPipeline(@Autowired(required = false) List<SupperPipeline> supperPipelineList) {
        ChannelPipeline channelPipeline = new ChannelPipeline();
        if (CollectionUtils.isNotEmpty(supperPipelineList)) {
            for (SupperPipeline supperPipeline : supperPipelineList) {
                channelPipeline.addLast(supperPipeline);
            }
        }
        return channelPipeline;
    }

}
