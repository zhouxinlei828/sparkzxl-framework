package com.github.sparkzxl.log.netty;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.utils.StrPool;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * description: WebSocket处理器，处理websocket连接相关
 *
 * @author zhouxinlei
 * @date 2021-06-05 10:06:08
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("与客户端建立连接，通道开启！");
        //添加到channelGroup通道组
        ChannelHandlerPool.channelGroup.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("与客户端断开连接，通道关闭！");
        //添加到channelGroup 通道组
        ChannelHandlerPool.channelGroup.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();

            Map<String, String> paramMap = getUrlParams(uri);
            System.out.println("接收到的参数是：" + JSON.toJSONString(paramMap));
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
            System.out.println("客户端收到服务器数据：" + text);

            String replyMsg = "333333";
            sendAllMessage(replyMsg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

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
            String[] arr = url.split(StrPool.SEMICOLON)[1].split("&");
            for (String s : arr) {
                String key = s.split("=")[0];
                String value = s.split("=")[1];
                map.put(key, value);
            }
            return map;

        } else {
            return map;
        }
    }
}
