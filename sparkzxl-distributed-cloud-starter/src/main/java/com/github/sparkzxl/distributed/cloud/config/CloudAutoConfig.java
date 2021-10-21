package com.github.sparkzxl.distributed.cloud.config;

import com.github.sparkzxl.distributed.cloud.event.CloudApplicationInitRunner;
import com.github.sparkzxl.distributed.cloud.loadbalancer.TopChoiceVersionIsolationRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: cloud application 配置
 *
 * @author zhouxinlei
 */
@Configuration
public class CloudAutoConfig {

    @Bean
    public CloudApplicationInitRunner cloudApplicationInitRunner() {
        return new CloudApplicationInitRunner();
    }

    @Bean
    public TopChoiceVersionIsolationRule topChoiceVersionIsolationRule() {
        return new TopChoiceVersionIsolationRule();
    }

}
