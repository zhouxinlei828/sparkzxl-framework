package com.github.sparkzxl.data.sync.admin.listener.websocket;

import static org.apache.tomcat.websocket.server.Constants.BINARY_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM;
import static org.apache.tomcat.websocket.server.Constants.TEXT_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM;

import com.github.sparkzxl.data.sync.admin.config.websocket.WebsocketProviderProperties;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * description: The Websocket configurator.
 *
 * @author zhouxinlei
 * @since 2022-08-25 15:25:59
 */
@ConditionalOnProperty(name = "sparkzxl.data.sync.websocket.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class WebsocketConfigurator extends ServerEndpointConfig.Configurator implements ServletContextInitializer {

    @Autowired
    private WebsocketProviderProperties websocketProviderProperties;

    @Override
    public void modifyHandshake(final ServerEndpointConfig sec, final HandshakeRequest request, final HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put(WebsocketListener.CLIENT_IP_NAME, httpSession.getAttribute(WebsocketListener.CLIENT_IP_NAME));
        super.modifyHandshake(sec, request, response);
    }

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        int messageMaxSize = websocketProviderProperties.getMessageMaxSize();
        if (messageMaxSize > 0) {
            servletContext.setInitParameter(TEXT_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM,
                    String.valueOf(messageMaxSize));
            servletContext.setInitParameter(BINARY_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM,
                    String.valueOf(messageMaxSize));
        }
    }
}
