package com.github.sparkzxl.lock;

import com.github.sparkzxl.lock.executor.LockExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * description: 锁信息对象
 *
 * @author zhouxinlei
 * @since 2022-05-01 21:47:10
 */
@Data
@AllArgsConstructor
public class LockInfo {

    /**
     * 线程id
     */
    private long threadId;
    /**
     * 锁名称
     */
    private String lockKey;

    /**
     * 锁值
     */
    private String lockValue;

    /**
     * 过期时间
     */
    private Long expire;

    /**
     * 获取锁超时时间
     */
    private Long acquireTimeout;

    /**
     * 获取锁次数
     */
    private int acquireCount;

    /**
     * 锁实例
     */
    private Object lockInstance;

    /**
     * 锁执行器
     */
    private LockExecutor lockExecutor;

}
