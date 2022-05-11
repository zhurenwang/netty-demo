package com.candy.handler;

import com.candy.message.CopyRequestMessage;
import com.candy.pool.ChannelPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class CopyChannelHandler extends SimpleChannelInboundHandler<CopyRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CopyRequestMessage msg) throws Exception {
        Channel channel = ChannelPool.getChannel(msg.getConnId());
        if (Objects.isNull(channel)) {
            return;
        }
        ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(msg.getData());
        channel.writeAndFlush(byteBuf);
    }
}
