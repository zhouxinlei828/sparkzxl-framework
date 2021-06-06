package com.github.sparkzxl.log.netty;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.utils.StrPool;
import com.github.sparkzxl.log.properties.LogProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * description: WebSocket处理器，处理websocket连接相关
 *
 * @author zhouxinlei
 * @date 2021-06-06 12:33:17
 */
@Slf4j
@ChannelHandler.Sharable
public class LogWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Map<String, Integer> LENGTH_MAP = Maps.newConcurrentMap();

    private String logPath;

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
            3,
            0,
            NANOSECONDS,
            new LinkedBlockingDeque<>(1000),
            new CustomizableThreadFactory());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端建立连接，通道开启！channelId:{}", ctx.channel().id().toString());
        //添加到channelGroup通道组
        ChannelHandlerPool.channelGroup.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端断开连接，通道关闭！channelId:{}", ctx.channel().id().toString());
        //添加到channelGroup 通道组
        ChannelHandlerPool.channelGroup.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();

            Map<String, String> paramMap = getUrlParams(uri);
            log.info("接收到的参数是：{}", JSON.toJSONString(paramMap));
            //如果url包含参数，需要处理
            if (uri.contains(StrPool.QUESTION_MARK)) {
                String newUri = uri.substring(0, uri.indexOf(StrPool.QUESTION_MARK));
                System.out.println(newUri);
                request.setUri(newUri);
            }

        } else if (msg instanceof TextWebSocketFrame) {
            //正常的TEXT消息类型
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            String text = frame.text();
            log.info("客户端收到服务器数据：{}", text);
            if (StringUtils.equals(text, "log")) {
                LENGTH_MAP.put(ctx.channel().id().toString(), 1);
                threadPoolExecutor.execute(() -> {
                    boolean first = true;
                    while (ctx.channel().isActive()) {
                        //日志文件路径，获取最新的
                        try {
                            File logFile = ResourceUtils.getFile(logPath);
                            RandomAccessFile randomFile = new RandomAccessFile(logFile, "rw");
                            randomFile.seek(LENGTH_MAP.get(ctx.channel().id().toString()));
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
                            LENGTH_MAP.put(ctx.channel().id().toString(), (int) randomFile.length());

                            //第一次如果太大，截取最新的1000行就够了，避免传输的数据太大
                            if (first && resourceList.size() > 1000) {
                                resourceList = ListUtil.sub(resourceList, resourceList.size() - 1000, resourceList.size());
                                first = false;
                            }

                            sendAllMessage(StringUtils.join(resourceList, "<br/>"));
                            //休眠半秒
                            Thread.sleep(500);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

    }

    private void sendAllMessage(String message) {
        //收到信息后，群发给所有channel
        ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }

    private static Map<String, String> getUrlParams(String url) {
        Map<String, String> map = Maps.newHashMap();
        url = url.replace(StrPool.QUESTION_MARK, StrPool.SEMICOLON);
        if (!url.contains(StrPool.SEMICOLON)) {
            return map;
        }
        if (url.split(StrPool.SEMICOLON).length > 0) {
            String[] arr = url.split(StrPool.SEMICOLON)[1].split(StrPool.AMPERSAND);
            for (String s : arr) {
                String key = s.split(StrPool.EQUALS)[0];
                String value = s.split(StrPool.EQUALS)[1];
                map.put(key, value);
            }
        }
        return map;
    }
}

