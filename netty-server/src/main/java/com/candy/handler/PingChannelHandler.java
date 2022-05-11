package com.candy.handler;

import com.candy.message.PingMessage;
import com.candy.message.PongMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PingChannelHandler extends SimpleChannelInboundHandler<PingMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception {
        PongMessage pongMessage = new PongMessage();
        ctx.writeAndFlush(pongMessage);
    }
}
