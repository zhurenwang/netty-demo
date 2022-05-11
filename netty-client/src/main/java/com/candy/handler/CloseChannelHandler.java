package com.candy.handler;

import com.candy.message.CloseChannelRequestMessage;
import com.candy.pool.ChannelPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class CloseChannelHandler extends SimpleChannelInboundHandler<CloseChannelRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloseChannelRequestMessage msg) throws Exception {
        String connId = msg.getConnId();
        Channel channel = ChannelPool.getChannel(connId);
        if (Objects.nonNull(channel)) {
            channel.close();
            ChannelPool.remove(connId);
        }

    }
}
