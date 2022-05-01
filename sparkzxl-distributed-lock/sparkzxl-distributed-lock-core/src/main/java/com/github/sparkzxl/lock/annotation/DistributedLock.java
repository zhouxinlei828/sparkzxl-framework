package com.github.sparkzxl.lock.annotation;

import com.github.sparkzxl.lock.autoconfigure.LockProperties;
import com.github.sparkzxl.lock.executor.LockExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 分布式锁注解
 *
 * @author zhouxinlei
 * @since 2022-03-26 17:14:32
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 用于多个方法锁同一把锁 可以理解为锁资源名称 为空则会使用 包名+类名+方法名
     *
     * @return 名称
     */
    String name() default "";

    /**
     * @return lock 执行器
     */
    Class<? extends LockExecutor> executor() default LockExecutor.class;

    /**
     * support SPel expression 锁的key = name + keys
     *
     * @return KEY
     */
    String[] keys() default "";

    /**
     * @return 过期时间 单位：毫秒
     * <pre>
     *     过期时间一定是要长于业务的执行时间. 未设置则为默认时间30秒 默认值：{@link LockProperties#expire}
     * </pre>
     */
    long expire() default -1;

    /**
     * @return 获取锁超时时间 单位：毫秒
     * <pre>
     *     结合业务,建议该时间不宜设置过长,特别在并发高的情况下. 未设置则为默认时间3秒 默认值：{acquireTimeout}
     * </pre>
     */
    long acquireTimeout() default -1;

    /**
     * 业务方法执行完后（方法内抛异常也算执行完）自动释放锁，如果为false，锁将不会自动释放直至到达过期时间才释放 {@link LockProperties#expire}
     *
     * @return 是否自动释放锁
     */
    boolean autoRelease() default true;

}
