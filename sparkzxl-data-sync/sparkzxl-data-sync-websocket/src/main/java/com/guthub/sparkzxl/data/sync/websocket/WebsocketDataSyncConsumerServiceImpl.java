package com.guthub.sparkzxl.data.sync.websocket;

import com.github.sparkzxl.data.sync.api.DataSyncService;
import com.google.common.collect.ImmutableMap;
import com.guthub.sparkzxl.data.sync.websocket.client.WebsocketReceiveClient;
import com.guthub.sparkzxl.data.sync.websocket.config.WebsocketConsumerProperties;
import com.guthub.sparkzxl.data.sync.websocket.handler.DataHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description: WebsocketDataSyncServiceImpl implementation for SyncDataService
 *
 * @author zhouxinlei
 * @since 2022-08-25 14:01:40
 */
public class WebsocketDataSyncConsumerServiceImpl implements DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketDataSyncConsumerServiceImpl.class);

    /**
     * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/tomcat/websocket/Constants.java#L99"/>
     */
    private static final String ORIGIN_HEADER_NAME = "Origin";

    private final List<WebsocketReceiveClient> clients = new ArrayList<>();

    /**
     * Instantiates a new Websocket sync cache.
     *
     * @param websocketConsumerProperties the websocket config
     * @param dataHandlerList       the dataHandlers
     */
    public WebsocketDataSyncConsumerServiceImpl(final WebsocketConsumerProperties websocketConsumerProperties,
                                                final List<DataHandler> dataHandlerList) {
        String[] urls = StringUtils.split(websocketConsumerProperties.getUrls(), ",");
        for (String url : urls) {
            try {
                if (StringUtils.isNotEmpty(websocketConsumerProperties.getAllowOrigin())) {
                    Map<String, String> headers = ImmutableMap.of(ORIGIN_HEADER_NAME, websocketConsumerProperties.getAllowOrigin());
                    clients.add(new WebsocketReceiveClient(new URI(url), headers, dataHandlerList));
                } else {
                    clients.add(new WebsocketReceiveClient(new URI(url), dataHandlerList));
                }
            } catch (URISyntaxException e) {
                logger.error("websocket url[{}] is error", url, e);
            }
        }
    }

    @Override
    public void close() throws Exception {
        for (WebsocketReceiveClient client : clients) {
            client.nowClose();
        }
    }
}
