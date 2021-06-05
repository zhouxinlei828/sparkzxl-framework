package com.github.sparkzxl.log.config;

import com.github.sparkzxl.log.aspect.WebLogAspect;
import com.github.sparkzxl.log.netty.NettyServer;
import com.github.sparkzxl.log.properties.LogProperties;
import com.github.sparkzxl.log.realtime.FileLogListening;
import com.github.sparkzxl.log.realtime.LoggerDisruptorQueue;
import com.github.sparkzxl.log.realtime.ProcessLogFilter;
import com.github.sparkzxl.log.realtime.disruptor.FileLoggerEventHandler;
import com.github.sparkzxl.log.realtime.disruptor.LoggerEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.net.InetAddress;

/**
 * description: 日志增强自动装配
 *
 * @author zhouxinlei
 * @date 2021-05-23 13:07:55
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(value = {LogProperties.class})
@Slf4j
public class LogAutoConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

    @Bean
    public WebLogAspect webLogAspect(LogProperties logProperties) {
        WebLogAspect webLogAspect = new WebLogAspect();
        webLogAspect.setStorage(logProperties.isStorage());
        return webLogAspect;
    }

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("*")
                .addInterceptors()
                .withSockJS();
    }

    @Bean
    public LoggerEventHandler loggerEventHandler(SimpMessagingTemplate messagingTemplate) {
        LoggerEventHandler loggerEventHandler = new LoggerEventHandler();
        loggerEventHandler.setMessagingTemplate(messagingTemplate);
        return loggerEventHandler;
    }

    @Bean
    public FileLoggerEventHandler fileLoggerEventHandler(SimpMessagingTemplate messagingTemplate) {
        FileLoggerEventHandler fileLoggerEventHandler = new FileLoggerEventHandler();
        fileLoggerEventHandler.setMessagingTemplate(messagingTemplate);
        return fileLoggerEventHandler;
    }

    @Bean
    public LoggerDisruptorQueue loggerDisruptorQueue(LoggerEventHandler loggerEventHandler, FileLoggerEventHandler fileLoggerEventHandler) {
        return new LoggerDisruptorQueue(loggerEventHandler, fileLoggerEventHandler);
    }

    @Bean
    public ProcessLogFilter processLogFilter() {
        return new ProcessLogFilter();
    }

    @Bean
    public FileLogListening fileLogListening(LogProperties logProperties) {
        FileLogListening fileLogListening = new FileLogListening();
        fileLogListening.setLogProperties(logProperties);
        return fileLogListening;
    }

    @Bean
    public NettyServer nettyServer() {
        Environment environment = applicationContext.getEnvironment();
        try {
            NettyServer nettyServer = new NettyServer(12345, "/ws");
            nettyServer.start();
            log.info("NettyServer 启动成功:{}:{}/{}",
                    InetAddress.getLocalHost().getHostAddress(),
                    environment.getProperty("server.port"), "/ws");
            return nettyServer;
        } catch (Exception e) {
            log.error("NettyServerError:{}", e.getMessage());
        }
        return null;
    }

}
