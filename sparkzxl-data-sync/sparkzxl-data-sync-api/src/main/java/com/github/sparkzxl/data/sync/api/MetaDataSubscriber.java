package com.github.sparkzxl.data.sync.api;


import com.github.sparkzxl.data.sync.common.entity.MetaData;

/**
 * description: The interface Meta data subscriber.
 *
 * @author zhouxinlei
 * @since 2022-08-24 10:49:25
 */
public interface MetaDataSubscriber {

    /**
     * On subscribe.
     *
     * @param metaData the meta data
     */
    void onSubscribe(MetaData metaData);

    /**
     * Un subscribe.
     *
     * @param metaData the meta data
     */
    void unSubscribe(MetaData metaData);

    /**
     * Refresh.
     */
    default void refresh() {
    }

}
