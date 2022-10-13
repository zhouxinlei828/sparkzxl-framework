package com.github.sparkzxl.data.sync.admin.listener;

import java.util.List;

/**
 * description: Event listener, used to send notification of event changes,
 * used to support HTTP, websocket, zookeeper and other event notifications.
 *
 * @author zhouxinlei
 * @since 2022-08-24 15:40:41
 */
public interface DataChangedListener {

    /**
     * On meta data changed.
     *
     * @param configGroup the config group
     * @param eventType   the event type
     * @param data        the changed data
     */
    default <T> void onChanged(String configGroup, String eventType, List<T> data) {

    }

}
