package com.github.sparkzxl.feign.resilience4j;

import io.github.resilience4j.feign.FeignDecorators;

/**
 * description: 用于包装FeignDecoratorBuilder
 * 可以用于实现 fallback
 *
 * @author zhouxinlei
 * @since 2022-04-03 20:14:21
 */
@FunctionalInterface
public interface FeignDecoratorBuilderInterceptor {

    void intercept(FeignDecorators.Builder builder);
}
