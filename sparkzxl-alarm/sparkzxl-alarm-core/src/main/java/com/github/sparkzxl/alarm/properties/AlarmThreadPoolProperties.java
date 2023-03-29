package com.github.sparkzxl.alarm.properties;

import com.github.sparkzxl.alarm.constant.AlarmConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 告警线程池参数配置-用于异步处理
 *
 * @author zhouxinlei
 * @since 2022-05-24 10:48:46
 */
@ConfigurationProperties(prefix = AlarmConstant.ALARM_THREAD_CONFIG_PREFIX)
public class AlarmThreadPoolProperties {

    private static final int DEFAULT_CORE_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * 线程池维护线程的最小数量, 选填
     */
    private int coreSize = DEFAULT_CORE_SIZE;
    /**
     * 线程池维护线程的最大数量, 选填
     */
    private int maxSize = DEFAULT_CORE_SIZE * 2;
    /**
     * 空闲线程的存活时间, 选填
     */
    private int keepAliveSeconds = 60;
    /**
     * 持有等待执行的任务队列, 选填
     */
    private int queueCapacity = 10;
    /**
     * 线程名称前缀, 选填
     */
    private String threadNamePrefix = AlarmConstant.DEFAULT_THREAD_NAME_PREFIX;

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }
}
