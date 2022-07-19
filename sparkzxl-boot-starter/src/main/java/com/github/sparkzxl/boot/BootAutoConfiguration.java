package com.github.sparkzxl.boot;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.github.sparkzxl.boot.undertow.UndertowServerFactoryCustomizer;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: boot自动装配
 *
 * @author zhouxinlei
 * @since 2021-07-25 18:10:07
 */
@EnableSpringUtil
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@Configuration
public class BootAutoConfiguration {

    @Bean
    public ApplicationLogRunner applicationRunner(ApplicationContext applicationContext) {
        return new ApplicationLogRunner(applicationContext);
    }

    @Bean
    @ConditionalOnClass(Undertow.class)
    public UndertowServerFactoryCustomizer getUndertowServerFactoryCustomizer() {
        return new UndertowServerFactoryCustomizer();
    }

    @Bean(name = "threadPoolTaskExecutor")
    public ExecutorService threadPoolTaskExecutor() {
        return TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(50, 300, 300,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy()));
    }

}
