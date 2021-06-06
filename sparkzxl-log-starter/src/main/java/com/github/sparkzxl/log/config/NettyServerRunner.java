package com.github.sparkzxl.log.config;

import com.github.sparkzxl.log.netty.LogWebSocketHandler;
import com.github.sparkzxl.log.netty.NettyServer;
import com.github.sparkzxl.log.properties.LogProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Objects;

/**
 * description: Application启动后运行
 * netty socket启动
 *
 * @author zhouxinlei
 */
@Slf4j
public class NettyServerRunner implements ApplicationRunner, Ordered {

    private final ApplicationContext applicationContext;
    private final LogProperties logProperties;

    public NettyServerRunner(ApplicationContext applicationContext, LogProperties logProperties) {
        this.applicationContext = applicationContext;
        this.logProperties = logProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Environment env = applicationContext.getEnvironment();
        String applicationName = env.getProperty("spring.application.name");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            String logPath = logProperties.getFile().getPath().concat("/") + applicationName + ".log";
            LogWebSocketHandler logWebSocketHandler = new LogWebSocketHandler();
            logWebSocketHandler.setLogPath(logPath);
            new NettyServer(port, "/websocket/logging", logWebSocketHandler).start();
            log.info("NettyServer 启动成功:{}:{}/{}",
                    InetAddress.getLocalHost().getHostAddress(),
                    port, "/websocket/logging");
        } catch (Exception e) {
            log.error("NettyServerError:{}", e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }
}
