package com.github.sparkzxl.feign.exception;

import com.github.sparkzxl.feign.util.NameUtils;
import feign.Response;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * description: 异常断言
 *
 * @author zhouxinlei
 * @since 2022-05-09 12:17:43
 */
@FunctionalInterface
public interface ExceptionPredicateFactory<C> extends Configurable<C> {

    default Predicate<Response> apply(Consumer<C> consumer) {
        C config = newConfig();
        consumer.accept(config);
        beforeApply(config);
        return apply(config);
    }

    /**
     * 前置
     *
     * @param config 配置
     */
    default void beforeApply(C config) {
    }

    /**
     * 自定义
     *
     * @param config 配置
     * @return Predicate<T>
     */
    ExceptionPredicate<Response> apply(C config);

    @Override
    default Class<C> getConfigClass() {
        throw new UnsupportedOperationException("getConfigClass() not implemented");
    }

    @Override
    default C newConfig() {
        throw new UnsupportedOperationException("newConfig() not implemented");
    }

    default String name() {
        return NameUtils.normalizeRoutePredicateName(getClass());
    }
}
