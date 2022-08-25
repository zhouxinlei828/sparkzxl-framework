package com.guthub.sparkzxl.data.sync.websocket.client;

import com.alibaba.fastjson.JSONObject;
import com.github.sparkzxl.data.common.entity.WebsocketData;
import com.github.sparkzxl.data.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.common.enums.DataEventTypeEnum;
import com.github.sparkzxl.data.common.timer.AbstractRoundTask;
import com.github.sparkzxl.data.common.timer.Timer;
import com.github.sparkzxl.data.common.timer.TimerTask;
import com.github.sparkzxl.data.common.timer.WheelTimerFactory;
import com.github.sparkzxl.sync.data.api.MetaDataSubscriber;
import com.guthub.sparkzxl.data.sync.websocket.handler.WebsocketDataHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * description: The type websocket client.
 *
 * @author zhouxinlei
 * @since 2022-08-25 13:45:02
 */
public class WebsocketReceiveClient extends WebSocketClient {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketReceiveClient.class);

    private volatile boolean alreadySync = Boolean.FALSE;

    private final WebsocketDataHandler websocketDataHandler;

    private final Timer timer;

    private TimerTask timerTask;

    /**
     * Instantiates a new websocket client.
     *
     * @param serverUri           the server uri
     * @param metaDataSubscribers the meta data subscribers
     */
    public WebsocketReceiveClient(final URI serverUri,
                                  final List<MetaDataSubscriber> metaDataSubscribers) {
        super(serverUri);
        this.websocketDataHandler = new WebsocketDataHandler(metaDataSubscribers);
        this.timer = WheelTimerFactory.getSharedTimer();
        this.connection();
    }

    private void connection() {
        this.connectBlocking();
        this.timer.add(timerTask = new AbstractRoundTask(null, TimeUnit.SECONDS.toMillis(10)) {
            @Override
            public void doRun(final String key, final TimerTask timerTask) {
                healthCheck();
            }
        });
    }

    @Override
    public boolean connectBlocking() {
        boolean success = false;
        try {
            success = super.connectBlocking();
        } catch (Exception ignored) {
        }
        if (success) {
            LOG.info("websocket connection server[{}] is successful.....", this.getURI().toString());
        } else {
            LOG.warn("websocket connection server[{}] is error.....", this.getURI().toString());
        }
        return success;
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        if (!alreadySync) {
            send(DataEventTypeEnum.MYSELF.name());
            alreadySync = true;
        }
    }

    @Override
    public void onMessage(final String result) {
        handleResult(result);
    }

    @Override
    public void onClose(final int i, final String s, final boolean b) {
        this.close();
    }

    @Override
    public void onError(final Exception e) {
        LOG.error("websocket server[{}] is error.....", getURI(), e);
    }

    @Override
    public void close() {
        alreadySync = false;
        if (this.isOpen()) {
            super.close();
        }
    }

    /**
     * Now close.
     * now close. will cancel the task execution.
     */
    public void nowClose() {
        this.close();
        timerTask.cancel();
    }

    private void healthCheck() {
        try {
            if (!this.isOpen()) {
                this.reconnectBlocking();
            } else {
                this.sendPing();
                LOG.debug("websocket send to [{}] ping message successful", this.getURI());
            }
        } catch (Exception e) {
            LOG.error("websocket connect is error :{}", e.getMessage());
        }
    }

    private void handleResult(final String result) {
        LOG.info("handleResult({})", result);
        WebsocketData<?> websocketData = JSONObject.parseObject(result, WebsocketData.class);
        ConfigGroupEnum groupEnum = ConfigGroupEnum.acquireByName(websocketData.getGroupType());
        String eventType = websocketData.getEventType();
        String json = JSONObject.toJSONString(websocketData.getData());
        websocketDataHandler.executor(groupEnum, json, eventType);
    }
}
