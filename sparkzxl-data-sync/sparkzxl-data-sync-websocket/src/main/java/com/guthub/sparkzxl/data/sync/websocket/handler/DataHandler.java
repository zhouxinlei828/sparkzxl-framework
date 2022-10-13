package com.guthub.sparkzxl.data.sync.websocket.handler;

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
     * @param json      the data for json
     * @param eventType the event type
     */
    void handle(String json, String eventType);

    /**
     * config  group
     *
     * @return String
     * @see com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum
     */
    String configGroup();
}
