package com.guthub.sparkzxl.data.sync.websocket.handler;


import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: The type Websocket cache handler.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:28:31
 */
public class WebsocketDataHandler {

    private static final Map<String, DataHandler> DATA_HANDLER_MAP = new HashMap<>(16);

    /**
     * Instantiates a new Websocket data handler.
     *
     * @param metaDataSubscribers  the meta data subscribers
     */
    public WebsocketDataHandler(final List<MetaDataSubscriber> metaDataSubscribers) {
        DATA_HANDLER_MAP.put(ConfigGroupEnum.META_DATA.name(), new MetaDataHandler(metaDataSubscribers));
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
