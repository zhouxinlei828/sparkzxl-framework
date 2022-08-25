package com.guthub.sparkzxl.data.sync.websocket;

import com.github.sparkzxl.sync.data.api.MetaDataSubscriber;
import com.github.sparkzxl.sync.data.api.DataCallService;
import com.guthub.sparkzxl.data.sync.websocket.client.WebsocketReceiveClient;
import com.guthub.sparkzxl.data.sync.websocket.config.WebsocketConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * description: WebsocketSyncDataService implementation for SyncDataService
 *
 * @author zhouxinlei
 * @since 2022-08-25 14:01:40
 */
public class WebsocketDataCallServiceImpl implements DataCallService {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketDataCallServiceImpl.class);

    private final List<WebsocketReceiveClient> clients = new ArrayList<>();

    /**
     * Instantiates a new Websocket sync cache.
     *
     * @param websocketConfig     the websocket config
     * @param metaDataSubscribers the meta data subscribers
     */
    public WebsocketDataCallServiceImpl(final WebsocketConfig websocketConfig,
                                        final List<MetaDataSubscriber> metaDataSubscribers) {
        String[] urls = StringUtils.split(websocketConfig.getUrls(), ",");
        for (String url : urls) {
            try {
                clients.add(new WebsocketReceiveClient(new URI(url), metaDataSubscribers));
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
