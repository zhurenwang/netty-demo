package com.candy.handler;

import com.candy.message.PongMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PongChannelHandler extends SimpleChannelInboundHandler<PongMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PongMessage msg) throws Exception {

    }
}
