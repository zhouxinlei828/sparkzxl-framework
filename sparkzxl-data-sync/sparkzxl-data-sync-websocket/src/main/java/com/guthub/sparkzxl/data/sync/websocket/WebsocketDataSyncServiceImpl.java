package com.guthub.sparkzxl.data.sync.websocket;

import com.github.sparkzxl.data.sync.api.DataCallService;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.google.common.collect.ImmutableMap;
import com.guthub.sparkzxl.data.sync.websocket.client.WebsocketReceiveClient;
import com.guthub.sparkzxl.data.sync.websocket.config.WebsocketClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description: WebsocketSyncDataService implementation for SyncDataService
 *
 * @author zhouxinlei
 * @since 2022-08-25 14:01:40
 */
public class WebsocketDataSyncServiceImpl implements DataCallService {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketDataSyncServiceImpl.class);

    /**
     * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/tomcat/websocket/Constants.java#L99"/>
     */
    private static final String ORIGIN_HEADER_NAME = "Origin";

    private final List<WebsocketReceiveClient> clients = new ArrayList<>();

    /**
     * Instantiates a new Websocket sync cache.
     *
     * @param websocketClientConfig the websocket config
     * @param metaDataSubscribers   the meta data subscribers
     */
    public WebsocketDataSyncServiceImpl(final WebsocketClientConfig websocketClientConfig,
                                        final List<MetaDataSubscriber> metaDataSubscribers) {
        String[] urls = StringUtils.split(websocketClientConfig.getUrls(), ",");
        for (String url : urls) {
            try {
                if (StringUtils.isNotEmpty(websocketClientConfig.getAllowOrigin())) {
                    Map<String, String> headers = ImmutableMap.of(ORIGIN_HEADER_NAME, websocketClientConfig.getAllowOrigin());
                    clients.add(new WebsocketReceiveClient(new URI(url), headers, metaDataSubscribers));
                } else {
                    clients.add(new WebsocketReceiveClient(new URI(url), metaDataSubscribers));
                }
            } catch (URISyntaxException e) {
                LOG.error("websocket url({}) is error", url, e);
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
