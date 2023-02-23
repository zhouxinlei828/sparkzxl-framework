package com.github.sparkzxl.boot.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

/**
 * description: Spring 线程池配置
 *
 * @author zhouxinlei
 * @since 2022-12-14 10:31:10
 */
@EnableAsync
@Configuration
@EnableConfigurationProperties(TaskExecutionProperties.class)
@AutoConfigureBefore(value = TaskExecutionAutoConfiguration.class)
@Slf4j
public class ThreadPoolAutoConfig {
    public ThreadPoolAutoConfig() {
        log.info("start init taskExecutor");
    }

    @Bean(name = {APPLICATION_TASK_EXECUTOR_BEAN_NAME, AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME})
    @Primary
    public Executor taskExecutor(TaskExecutionProperties taskExecutionProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        TaskExecutionProperties.Pool pool = taskExecutionProperties.getPool();
        //核心线程池大小
        executor.setCorePoolSize(pool.getCoreSize());
        //最大线程数
        executor.setMaxPoolSize(pool.getMaxSize());
        //队列容量
        executor.setQueueCapacity(pool.getQueueCapacity());
        //活跃时间
        executor.setKeepAliveSeconds((int) pool.getKeepAlive().getSeconds());
        //线程名字前缀
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());
        TaskExecutionProperties.Shutdown shutdown = taskExecutionProperties.getShutdown();

        // 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(shutdown.isAwaitTermination());
        // 设置这个执行器在关闭时应该阻止的最大秒数
        executor.setAwaitTerminationSeconds(30);
        // 线程池对拒绝任务的处理策略,当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }
}
