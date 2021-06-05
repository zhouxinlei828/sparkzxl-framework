package com.github.sparkzxl.log.config;


import com.github.sparkzxl.core.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.ResourceUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * description: WebSocket获取实时日志并输出到Web页面
 *
 * @author zhouxinlei
 * @date 2021-06-05 12:31:54
 */
@Slf4j
@ServerEndpoint(value = "/websocket/logging", configurator = MyEndpointConfigure.class)
public class LoggingWsServer {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 连接集合
     */
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> LENGTH_MAP = new ConcurrentHashMap<>();

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
            3,
            0,
            NANOSECONDS,
            new LinkedBlockingDeque<>(1000),
            new CustomizableThreadFactory());

    /**
     * 连接建立成功调用的方法
     *
     * @param session 连接
     */
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
                String logPath = System.getProperty("user.home") + "/logs/" + applicationName + ".log";
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
                                    .replaceAll("\"", "&quot;")
                                    .replace(" ", StrPool.HTML_NBSP)
                                    .replace("\tat", StrPool.HTML_NBSP);

                            //处理等级
                            log = log.replace("DEBUG", "<span style='color: black;'>DEBUG</span>");
                            log = log.replace("INFO", "<span style='color: #3FB1F5;'>INFO</span>");
                            log = log.replace("WARN", "<span style='color: #F16372;'>WARN</span>");
                            log = log.replace("ERROR", "<span style='color: red;'>ERROR</span>");
                            log = log.replace("application", "<span style='color: #3FB1F5;'>application</span>");
                            //处理类名
                            String regex = "\\[(.*?)]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(log);
                            if (matcher.find()) {
                                String group = matcher.group(1);
                                log = log.replace(group, "<span style='color: #298a8a;'>" + group + "</span>");
                            }
                            result = result.concat(log).concat("<br/>");
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

    /**
     * 连接关闭调用的方法
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        //从集合中删除
        SESSION_MAP.remove(session.getId());
        LENGTH_MAP.remove(session.getId());
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * 封装一个send方法，发送消息到前端
     *
     * @param session
     * @param message
     */
    private void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
