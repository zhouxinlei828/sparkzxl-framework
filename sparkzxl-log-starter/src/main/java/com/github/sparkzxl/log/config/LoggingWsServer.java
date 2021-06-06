package com.github.sparkzxl.log.config;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import com.github.sparkzxl.core.utils.StrPool;
import com.github.sparkzxl.log.properties.LogProperties;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.ResourceUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Autowired
    private LogProperties logProperties;


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
            boolean first = true;
            while (SESSION_MAP.get(session.getId()) != null) {
                //日志文件路径，获取最新的
                String logPath = logProperties.getFile().getPath().concat("/") + applicationName + ".log";
                try {
                    File logFile = ResourceUtils.getFile(logPath);
                    RandomAccessFile randomFile = new RandomAccessFile(logFile, "rw");
                    try {
                        randomFile.seek(LENGTH_MAP.get(session.getId()));
                        String tmp;
                        List<String> resourceList = Lists.newArrayList();
                        while ((tmp = randomFile.readLine()) != null) {
                            String log = new String(tmp.getBytes("ISO8859-1"));
                            String escapeLog = HtmlUtil.escape(log);
                            escapeLog = escapeLog.replaceAll("\"", "&quot;")
                                    .replaceAll("\\s", StrPool.HTML_NBSP);

                            //处理等级
                            escapeLog = escapeLog.replace("DEBUG", "<span style='color: black;'>DEBUG</span>");
                            escapeLog = escapeLog.replace("INFO", "<span style='color: #3FB1F5;'>INFO</span>");
                            escapeLog = escapeLog.replace("WARN", "<span style='color: #F16372;'>WARN</span>");
                            escapeLog = escapeLog.replace("ERROR", "<span style='color: red;'>ERROR</span>");
                            escapeLog = escapeLog.replace("application", "<span style='color: #3FB1F5;'>application</span>");
                            //处理类名
                            String regex = "\\[(.*?)]";
                            String result = ReUtil.get(regex, escapeLog, 1);
                            if (StringUtils.isNotBlank(result)) {
                                escapeLog = escapeLog.replace(result, "<span style='color: #298a8a;'>" + result + "</span>");
                            }
                            resourceList.add(escapeLog);
                        }
                        LENGTH_MAP.put(session.getId(), (int) randomFile.length());

                        //第一次如果太大，截取最新的1000行就够了，避免传输的数据太大
                        if (first && resourceList.size() > 1000) {
                            resourceList = ListUtil.sub(resourceList, resourceList.size() - 200, resourceList.size());
                            first = false;
                        }

                        send(session, StringUtils.join(resourceList, "<br/>"));
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
     * @param session session
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
     * @param session session
     * @param error   异常信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     *
     * @param message 客户端消息
     * @param session session
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * 封装一个send方法，发送消息到前端
     *
     * @param session session
     * @param message 发送消息
     */
    private void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
