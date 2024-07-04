package com.github.sparkzxl.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * description: Customizes the parameterized class.
 *
 * @author zhouxinlei
 * @since 2024-07-01 15:27:03
 */
public interface Customizer<TOCUSTOMIZE> {

    void customize(TOCUSTOMIZE tocustomize);

    /**
     * Create a wrapped customizer that guarantees that the {@link #customize(Object)}
     * method of the delegated <code>customizer</code> is called at most once per target.
     *
     * @param customizer a customizer to be delegated
     * @param keyMapper  a mapping function to produce the identifier of the target
     * @param <T>        the type of the target to customize
     * @param <K>        the type of the identifier of the target
     * @return a wrapped customizer
     */
    static <T, K> Customizer<T> once(Customizer<T> customizer, Function<? super T, ? extends K> keyMapper) {
        final ConcurrentMap<K, Boolean> customized = new ConcurrentHashMap<>();
        return t -> {
            final K key = keyMapper.apply(t);
            customized.computeIfAbsent(key, k -> {
                customizer.customize(t);
                return true;
            });
        };
    }

}
