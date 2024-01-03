package com.github.sparkzxl.data.sync.websocket.handler;

import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

/**
 * description: The interface Data handler.
 *
 * @author zhouxinlei
 * @since 2022-08-25 13:42:29
 */
public interface DataHandler {

    /**
     * Handle data json response.
     *
     * @param syncId    synchronization id
     * @param json      the data for json
     * @param eventType the event type
     */
    void handle(String syncId, String json, String eventType);

    /**
     * config  group
     *
     * @return String
     * @see ConfigGroupEnum
     */
    String configGroup();
}
