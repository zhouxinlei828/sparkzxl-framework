package com.github.sparkzxl.data.admin.listener;

import com.github.sparkzxl.data.common.entity.MetaData;
import com.github.sparkzxl.data.common.enums.DataEventTypeEnum;

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
     * @param changed   the changed
     * @param eventType the event type
     */
    default void onMetaDataChanged(List<MetaData> changed, DataEventTypeEnum eventType) {

    }

}
