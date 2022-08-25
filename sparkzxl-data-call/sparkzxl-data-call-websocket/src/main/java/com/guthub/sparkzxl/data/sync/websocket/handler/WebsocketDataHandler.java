package com.guthub.sparkzxl.data.sync.websocket.handler;


import com.github.sparkzxl.data.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.sync.data.api.MetaDataSubscriber;

import java.util.EnumMap;
import java.util.List;

/**
 * description: The type Websocket cache handler.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:28:31
 */
public class WebsocketDataHandler {

    private static final EnumMap<ConfigGroupEnum, DataHandler> ENUM_MAP = new EnumMap<>(ConfigGroupEnum.class);

    /**
     * Instantiates a new Websocket data handler.
     *
     * @param metaDataSubscribers  the meta data subscribers
     */
    public WebsocketDataHandler(final List<MetaDataSubscriber> metaDataSubscribers) {
        ENUM_MAP.put(ConfigGroupEnum.META_DATA, new MetaDataHandler(metaDataSubscribers));
    }

    /**
     * Executor.
     *
     * @param type      the type
     * @param json      the json
     * @param eventType the event type
     */
    public void executor(final ConfigGroupEnum type, final String json, final String eventType) {
        ENUM_MAP.get(type).handle(json, eventType);
    }
}
