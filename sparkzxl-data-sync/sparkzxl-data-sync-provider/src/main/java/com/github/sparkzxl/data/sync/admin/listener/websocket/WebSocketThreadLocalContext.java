package com.github.sparkzxl.data.sync.admin.listener.websocket;

import cn.hutool.core.convert.Convert;
import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.HashMap;
import java.util.Map;

/**
 * description: The interface for the websocket ThreadLocal Context
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:00:16
 */
public class WebSocketThreadLocalContext {

    private static final ThreadLocal<Map<String, Object>> THREAD_CONTEXT = new TransmittableThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>(16);
        }
    };

    public static void put(final String key, final Object value) {
        THREAD_CONTEXT.get().put(key, value);
    }

    /**
     * remove thread variable.
     *
     * @param key remove key
     */
    public static void remove(final String key) {
        THREAD_CONTEXT.get().remove(key);
    }

    /**
     * get thread variables.
     *
     * @param key get key
     * @return the Object
     */
    public static <T> T get(final String key, Class<T> type) {
        Object o = THREAD_CONTEXT.get().get(key);
        return Convert.convert(type, o);
    }

    /**
     * remove all variables.
     */
    public static void clear() {
        THREAD_CONTEXT.remove();
    }


}
