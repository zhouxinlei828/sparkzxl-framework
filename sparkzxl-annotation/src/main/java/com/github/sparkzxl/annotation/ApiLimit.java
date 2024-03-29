package com.github.sparkzxl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiLimit {

    /**
     * 资源的key,唯一
     * 作用：不同的接口，不同的流量控制
     */
    String key() default "";

    /**
     * 限制次数（每秒）
     */
    double permitsPerSecond();

    /**
     * 最大等待时间
     */
    long timeOut() default 0L;

    /**
     * 最大等待时间的单位，默认毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 获取不到令牌时候的提示语
     */
    String message() default "请求过于频繁，请稍后重试";
}
