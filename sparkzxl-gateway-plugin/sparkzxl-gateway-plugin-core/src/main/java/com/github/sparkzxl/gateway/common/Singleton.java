package com.github.sparkzxl.gateway.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: Singleton
 *
 * @author zhouxinlei
 * @since 2022-08-15 14:53:50
 */
public enum Singleton {

    /**
     * Inst singleton.
     */
    INSTANCE;

    /**
     * The Singles.
     */
    private static final Map<String, Object> SINGLES = new ConcurrentHashMap<>();

    /**
     * Single.
     *
     * @param clazz the clazz
     * @param o     the o
     */
    public void single(final Class<?> clazz, final Object o) {
        SINGLES.put(clazz.getName(), o);
    }

    /**
     * Get t.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> clazz) {
        return (T) SINGLES.get(clazz.getName());
    }
}
