package com.github.sparkzxl.data.sync.websocket.handler;


import org.apache.commons.lang3.ObjectUtils;
import com.github.sparkzxl.data.sync.api.BusinessDataSubscriber;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(WebsocketDataConsumerHandler.class);

    private static final Map<String, DataHandler> DATA_HANDLER_MAP = new HashMap<>(16);

    /**
     * Instantiates a new Websocket data handler.
     *
     * @param metaDataSubscribers the dataHandlers
     */
    public WebsocketDataConsumerHandler(List<MetaDataSubscriber> metaDataSubscribers,
                                        List<BusinessDataSubscriber> businessDataSubscribers) {
        DATA_HANDLER_MAP.put(ConfigGroupEnum.META_DATA.getCode(), new MetaDataHandler(metaDataSubscribers));
        DATA_HANDLER_MAP.put(ConfigGroupEnum.BUSINESS_DATA.getCode(), new BusinessDataHandler(businessDataSubscribers));
    }

    /**
     * Executor.
     *
     * @param syncId      synchronization id
     * @param configGroup the config group
     * @param json        the json
     * @param eventType   the event type
     */
    public void executor(final String syncId, final String configGroup, final String json, final String eventType) {
        DataHandler dataHandler = DATA_HANDLER_MAP.get(configGroup);
        if (ObjectUtils.isEmpty(dataHandler)) {
            logger.error("The interface [{}] Data handler not found.", configGroup);
        }
        dataHandler.handle(syncId, json, eventType);
    }
}
