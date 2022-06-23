package com.github.sparkzxl.lock.autoconfigure;

import com.github.sparkzxl.lock.executor.LockExecutor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: lock配置
 *
 * @author zhouxinlei
 * @since 2022-05-01 22:32:45
 */
@Data
@ConfigurationProperties(prefix = "distributedlock")
public class DistributedLockProperties {

    /**
     * 过期时间 单位：毫秒
     */
    private Long expire = 30000L;

    /**
     * 获取锁超时时间 单位：毫秒
     */
    private Long acquireTimeout = 3000L;

    /**
     * 获取锁失败时重试时间间隔 单位：毫秒
     */
    private Long retryInterval = 100L;

    /**
     * 默认执行器，不设置默认取容器第一个(默认注入顺序，redisson>redisTemplate>zookeeper)
     */
    private Class<? extends LockExecutor> primaryExecutor;

    /**
     * 锁key前缀
     */
    private String lockKeyPrefix = "distributed-lock";
}
