package com.github.sparkzxl.data.sync.websocket;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.sparkzxl.core.task.AbstractRoundTask;
import com.github.sparkzxl.core.task.WheelTimerFactory;
import com.github.sparkzxl.core.task.timer.Timer;
import com.github.sparkzxl.core.task.timer.TimerTask;
import com.github.sparkzxl.core.util.ListUtils;
import com.github.sparkzxl.data.sync.api.BusinessDataSubscriber;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.github.sparkzxl.data.sync.websocket.client.WebsocketReceiveClient;
import com.github.sparkzxl.data.sync.websocket.config.WebsocketConsumerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description: WebsocketDataSyncServiceImpl implementation for WebsocketDataSyncService
 *
 * @author zhouxinlei
 * @since 2022-08-25 14:01:40
 */
public class WebsocketDataSyncServiceImpl implements WebsocketDataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketDataSyncServiceImpl.class);

    /**
     * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/tomcat/websocket/Constants.java#L99"/>
     */
    private static final String ORIGIN_HEADER_NAME = "Origin";
    private final Timer timer;
    private final DiscoveryClient discoveryClient;
    private final Map<String, WebsocketReceiveClient> clients = Maps.newConcurrentMap();
    private final WebsocketConsumerProperties consumerProperties;
    private final List<MetaDataSubscriber> metaDataSubscribers;
    private final List<BusinessDataSubscriber> businessDataSubscribers;
    private TimerTask timerTask;

    /**
     * Instantiates a new Websocket sync cache.
     */
    public WebsocketDataSyncServiceImpl(DiscoveryClient discoveryClient,
                                        WebsocketConsumerProperties consumerProperties,
                                        List<MetaDataSubscriber> metaDataSubscribers,
                                        List<BusinessDataSubscriber> businessDataSubscribers) {
        this.discoveryClient = discoveryClient;
        this.consumerProperties = consumerProperties;
        this.metaDataSubscribers = metaDataSubscribers;
        this.businessDataSubscribers = businessDataSubscribers;
        this.timer = WheelTimerFactory.getSharedTimer();
        boolean useRegistry = consumerProperties.isUseRegistry();
        if (!useRegistry) {
            if (StringUtils.isEmpty(consumerProperties.getUrls())) {
                throw new RuntimeException("websocket client urls is not empty.");
            }
            String[] urls = StringUtils.split(consumerProperties.getUrls(), ",");
            addAllClient(Arrays.asList(urls));
        } else {
            connectClient();
        }

    }

    private void connectClient() {
        String serviceId = consumerProperties.getServiceId();
        this.timer.add(timerTask = new AbstractRoundTask(null, TimeUnit.SECONDS.toMillis(15)) {
            @Override
            public void doRun(final String key, final TimerTask timerTask) {
                List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
                if (CollectionUtils.isNotEmpty(serviceInstanceList)) {
                    List<String> urlList = Lists.newArrayList();
                    for (ServiceInstance serviceInstance : serviceInstanceList) {
                        String url = MessageFormat.format("ws://{0}:{1}/websocket", serviceInstance.getHost(), String.valueOf(serviceInstance.getPort()));
                        urlList.add(url);
                    }
                    addAllClient(urlList);
                } else {
                    clearClient();
                }
            }
        });
    }

    private void addAllClient(List<String> urlList) {
        try {
            for (String url : urlList) {
                if (ObjectUtils.isNotEmpty(clients.get(url))) {
                    return;
                }
                if (StringUtils.isNotEmpty(consumerProperties.getAllowOrigin())) {
                    Map<String, String> headers = ImmutableMap.of(ORIGIN_HEADER_NAME, consumerProperties.getAllowOrigin());
                    clients.put(url, new WebsocketReceiveClient(new URI(url), headers, metaDataSubscribers, businessDataSubscribers));
                } else {
                    clients.put(url, new WebsocketReceiveClient(new URI(url), metaDataSubscribers, businessDataSubscribers));
                }
            }
            List<String> clientList = new ArrayList<>(clients.keySet());
            List<String> differencedList = ListUtils.differenceList(clientList, urlList);
            if (CollectionUtils.isNotEmpty(differencedList)) {
                for (String url : differencedList) {
                    removeClient(url);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("clients:{}", clients.keySet());
            }
        } catch (URISyntaxException e) {
            logger.error("websocket url[{}] is error", urlList, e);
        }
    }

    public void removeClient(final String url) {
        WebsocketReceiveClient client = clients.get(url);
        if (ObjectUtils.isNotEmpty(client)) {
            client.nowClose();
        }
        clients.remove(url);
    }

    public void clearClient() {
        for (WebsocketReceiveClient client : clients.values()) {
            client.nowClose();
        }
        clients.clear();
    }

    @Override
    public void close() throws Exception {
        for (WebsocketReceiveClient client : clients.values()) {
            client.nowClose();
        }
        timerTask.cancel();
    }
}
