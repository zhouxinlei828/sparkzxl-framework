package com.github.sparkzxl.data.admin.listener.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * description: The Websocket listener.
 *
 * @author zhouxinlei
 * @since 2022-08-25 15:23:03
 */
@WebListener
@Configuration
public class WebsocketListener implements ServletRequestListener {

    public static final String CLIENT_IP_NAME = "ClientIP";

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketListener.class);

    @Override
    public void requestDestroyed(final ServletRequestEvent sre) {
        try {
            HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
            if (Objects.nonNull(request) && Objects.nonNull(request.getSession())) {
                HttpSession session = request.getSession();
                request.removeAttribute(CLIENT_IP_NAME);
                session.removeAttribute(CLIENT_IP_NAME);
            }
        } catch (Exception e) {
            LOG.error("request destroyed error", e);
        }
    }

    @Override
    public void requestInitialized(final ServletRequestEvent sre) {
        try {
            HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
            if (Objects.nonNull(request) && Objects.nonNull(request.getSession())) {
                HttpSession session = request.getSession();
                request.setAttribute(CLIENT_IP_NAME, sre.getServletRequest().getRemoteAddr());
                session.setAttribute(CLIENT_IP_NAME, sre.getServletRequest().getRemoteAddr());
            }
        } catch (Exception e) {
            LOG.error("request initialized error", e);
        }
    }
}
