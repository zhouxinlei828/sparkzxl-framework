package com.github.sparkzxl.log.netty;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * description: 通道组池，管理所有websocket连接
 *
 * @author zhouxinlei
 * @date 2021-06-06 09:27
 */
public class ChannelHandlerPool {

    public ChannelHandlerPool() {
    }

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
