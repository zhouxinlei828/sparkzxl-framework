/*
package com.github.sparkzxl.log;


import com.github.sparkzxl.log.config.MyEndpointConfigure;
import com.github.sparkzxl.log.realtime.LoggerDisruptorQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

*/
/**
 * description: WebSocket获取实时日志并输出到Web页面
 *
 * @author zhouxinlei
 * @date 2021-06-05 09:25:03
 *//*

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/logging", configurator = MyEndpointConfigure.class)
public class LoggingWSServer {

    @Value("${spring.application.name}")
    private String applicationName;

    */
/**
     * 连接集合
     *//*

    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> LENGTH_MAP = new ConcurrentHashMap<>();

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
            3,
            0,
            NANOSECONDS,
            new LinkedBlockingDeque<>(1000),
            new CustomizableThreadFactory());

    */
/**
     * 连接建立成功调用的方法
     *//*

    @OnOpen
    public void onOpen(Session session) {
        //添加到集合中
        SESSION_MAP.put(session.getId(), session);
        //默认从第一行开始
        LENGTH_MAP.put(session.getId(), 1);

        log.info("LoggingWebSocketServer 任务开始");
        threadPoolExecutor.execute(() -> {
            while (SESSION_MAP.get(session.getId()) != null) {
                //日志文件路径，获取最新的
                String logPath = System.getProperty("user.home") + "/log/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + applicationName + ".log";
                try {
                    File logFile = ResourceUtils.getFile(logPath);
                    RandomAccessFile randomFile = new RandomAccessFile(logFile, "rw");
                    try {
                        randomFile.seek(LENGTH_MAP.get(session.getId()));
                        String tmp;
                        String result = "";
                        while ((tmp = randomFile.readLine()) != null) {
                            String log = new String(tmp.getBytes("ISO8859-1"));
                            log = log.replaceAll("&", "&amp;")
                                    .replaceAll("<", "&lt;")
                                    .replaceAll(">", "&gt;")
                                    .replaceAll("\"", "&quot;");

                            //处理等级
                            log = log.replace("DEBUG", "<span style='color: blue;'>DEBUG</span>");
                            log = log.replace("INFO", "<span style='color: green;'>INFO</span>");
                            log = log.replace("WARN", "<span style='color: orange;'>WARN</span>");
                            log = log.replace("ERROR", "<span style='color: red;'>ERROR</span>");
                            //处理类名
                            String[] split = log.split("]");
                            if (split.length >= 2) {
                                String[] split1 = split[1].split("-");
                                if (split1.length >= 2) {
                                    log = split[0] + "]" + "<span style='color: #298a8a;'>" + split1[0] + "</span>" + "-" + split1[1];
                                }
                            }
                            result = result.concat(log).concat("<br/>");
                            LoggerDisruptorQueue.publishEvent(log);
                        }
                        LENGTH_MAP.put(session.getId(), (int) randomFile.length());
                        //发送
                        send(session, result);
                        //休眠一秒
                        Thread.sleep(1000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.info("LoggingWebSocketServer 任务结束");
        });
    }

    */
/**
     * 连接关闭调用的方法
     *//*

    @OnClose
    public void onClose(Session session) {
        //从集合中删除
        SESSION_MAP.remove(session.getId());
        LENGTH_MAP.remove(session.getId());
    }

    */
/**
     * 发生错误时调用
     *//*

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    */
/**
     * 服务器接收到客户端消息时调用的方法
     *//*

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    */
/**
     * 封装一个send方法，发送消息到前端
     *//*

    private void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
