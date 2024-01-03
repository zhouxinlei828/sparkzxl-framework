package com.github.sparkzxl.data.sync.api;

import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

/**
 * description: The interface data subscriber.
 *
 * @author zhouxinlei
 * @since 2022-09-02 14:17:42
 */
public interface DataSubscriber<T> {

    /**
     * On subscribe.
     *
     * @param syncId synchronization id
     * @param t      the data
     */
    void onSubscribe(String syncId, T t);

    /**
     * Un subscribe.
     *
     * @param syncId synchronization id
     * @param t      the data
     */
    void unSubscribe(String syncId, T t);

    /**
     * Refresh.
     */
    default void refresh() {
    }

    /**
     * data group
     *
     * @return String
     * @see ConfigGroupEnum
     */
    String group();
}
