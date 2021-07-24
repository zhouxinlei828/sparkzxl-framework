package com.github.sparkzxl.boot;

import com.github.sparkzxl.boot.application.event.ApplicationInitRunner;
import com.github.sparkzxl.boot.infrastructure.annonation.EnableSpringUtil;
import com.github.sparkzxl.log.netty.NettyServer;
import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * description: sparkzxl boot 启动类
 *
 * @author zhouxinlei
 */
@EnableSpringUtil
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
public class SparkBootApplication {

    @Bean
    public ApplicationInitRunner applicationRunner(ApplicationContext applicationContext) {
        return new ApplicationInitRunner(applicationContext);
    }

    /**
     * 启动netty server
     *
     * @param port              端口
     * @param websocketPath     地址
     * @param busChannelHandler 业务处理
     */
    public static void startNettyServer(Integer port, String websocketPath, ChannelHandler busChannelHandler) {
        NettyServer nettyServer = new NettyServer(ObjectUtils.isEmpty(port) ? 8762 : port,
                StringUtils.isNotBlank(websocketPath) ? websocketPath : "/websocket/logging",
                busChannelHandler);
        nettyServer.start();
        try {
            log.info("NettyServer 启动成功:{}:{}/{}",
                    InetAddress.getLocalHost().getHostAddress(),
                    8672, "/websocket/logging");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
