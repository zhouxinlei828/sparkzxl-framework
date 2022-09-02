package com.guthub.sparkzxl.data.sync.websocket.handler;


import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: The type Websocket cache handler.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:28:31
 */
public class WebsocketDataConsumerHandler {

    private static final Map<String, DataHandler> DATA_HANDLER_MAP = new HashMap<>(16);

    /**
     * Instantiates a new Websocket data handler.
     *
     * @param dataHandlerList the dataHandlers
     */
    public WebsocketDataConsumerHandler(final List<DataHandler> dataHandlerList) {
        for (DataHandler dataHandler : dataHandlerList) {
            DATA_HANDLER_MAP.put(dataHandler.group(), dataHandler);
        }
    }

    /**
     * Executor.
     *
     * @param type      the type
     * @param json      the json
     * @param eventType the event type
     */
    public void executor(final ConfigGroupEnum type, final String json, final String eventType) {
        DATA_HANDLER_MAP.get(type.name()).handle(json, eventType);
    }
}
