package com.github.sparkzxl.data.sync.api;

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
     * @param t the data
     */
    void onSubscribe(T t);

    /**
     * Un subscribe.
     *
     * @param t the data
     */
    void unSubscribe(T t);

    /**
     * Refresh.
     */
    default void refresh() {
    }

    /**
     * data group
     *
     * @return String
     * @see com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum
     */
    String group();
}
