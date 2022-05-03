package com.github.sparkzxl.lock.autoconfigure;

import com.github.sparkzxl.lock.*;
import com.github.sparkzxl.lock.aop.DistributedLockAspect;
import com.github.sparkzxl.lock.executor.LockExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description: 分布式锁自动配置器
 *
 * @author zhouxinlei
 * @since 2022-05-01 22:02:36
 */
@Configuration
@EnableConfigurationProperties(DistributedLockProperties.class)
@RequiredArgsConstructor
public class LockAutoConfiguration {

    private final DistributedLockProperties properties;

    @SuppressWarnings("rawtypes")
    @Bean
    @ConditionalOnMissingBean
    public LockTemplate lockTemplate(List<LockExecutor> executors) {
        LockTemplate lockTemplate = new LockTemplate();
        lockTemplate.setProperties(properties);
        lockTemplate.setExecutors(executors);
        return lockTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockKeyBuilder lockKeyBuilder() {
        return new DefaultLockKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public LockFailureStrategy lockFailureStrategy() {
        return new DefaultLockFailureStrategy();
    }

    @Bean
    public DistributedLockAspect distributedLockAspect(LockTemplate lockTemplate, LockKeyBuilder lockKeyBuilder,
                                                       LockFailureStrategy lockFailureStrategy) {
        return new DistributedLockAspect(lockTemplate, lockKeyBuilder, lockFailureStrategy, properties);
    }

}
