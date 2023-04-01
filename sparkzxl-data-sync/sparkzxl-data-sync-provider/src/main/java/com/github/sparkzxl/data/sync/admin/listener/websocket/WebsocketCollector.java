package com.github.sparkzxl.data.sync.admin.listener.websocket;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.data.sync.admin.DataSyncService;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description: The type Websocket data changed listener.
 *
 * @author zhouxinlei
 * @since 2022-08-25 10:56:33
 */
@ServerEndpoint(value = "/websocket", configurator = WebsocketConfigurator.class)
public class WebsocketCollector {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketCollector.class);

    private static final Set<Session> SESSION_SET = new CopyOnWriteArraySet<>();

    private static final String SESSION_KEY = "sessionKey";

    private static String getClientIp(final Session session) {
        Map<String, Object> userProperties = session.getUserProperties();
        if (MapUtils.isEmpty(userProperties)) {
            return StringUtils.EMPTY;
        }

        return Optional.ofNullable(userProperties.get(WebsocketListener.CLIENT_IP_NAME))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
    }

    /**
     * Send.
     *
     * @param message the message
     * @param type    the type
     */
    public static void send(final String message, final DataEventTypeEnum type) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        if (DataEventTypeEnum.MYSELF == type) {
            Session session = WebSocketThreadLocalContext.get(SESSION_KEY, Session.class);
            if (Objects.nonNull(session)) {
                sendMessageBySession(session, message);
            }
        } else {
            SESSION_SET.forEach(session -> sendMessageBySession(session, message));
        }

    }

    private static synchronized void sendMessageBySession(final Session session, final String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.error("websocket send result is exception: ", e);
        }
    }

    /**
     * On open.
     *
     * @param session the session
     */
    @OnOpen
    public void onOpen(final Session session) {
        logger.info("websocket on client[{}] open successful,maxTextMessageBufferSize:{}",
                getClientIp(session), session.getMaxTextMessageBufferSize());
        SESSION_SET.add(session);
    }

    /**
     * On message.
     *
     * @param message the message
     * @param session the session
     */
    @OnMessage
    public void onMessage(final String message, final Session session) {
        if (!Objects.equals(message, DataEventTypeEnum.MYSELF.name())) {
            return;
        }
        try {
            WebSocketThreadLocalContext.put(SESSION_KEY, session);
            SpringContextUtils.getBean(DataSyncService.class).syncAll(DataEventTypeEnum.MYSELF);
        } finally {
            WebSocketThreadLocalContext.clear();
        }
    }

    /**
     * On close.
     *
     * @param session the session
     */
    @OnClose
    public void onClose(final Session session) {
        clearSession(session);
        logger.warn("websocket close on client[{}]", getClientIp(session));
    }

    /**
     * On error.
     *
     * @param session the session
     * @param error   the error
     */
    @OnError
    public void onError(final Session session, final Throwable error) {
        clearSession(session);
        logger.error("websocket collection on client[{}] error: ", getClientIp(session), error);
    }

    private void clearSession(final Session session) {
        SESSION_SET.remove(session);
        WebSocketThreadLocalContext.clear();
    }
}
