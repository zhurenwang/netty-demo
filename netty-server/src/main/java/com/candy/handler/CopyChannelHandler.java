package com.candy.handler;

import com.candy.connector.ChannelFactory;
import com.candy.message.ConnectRequestMessage;
import com.candy.message.ConnectResponseMessage;
import com.candy.message.CopyRequestMessage;
import com.candy.pool.ChannelPool;
import com.candy.pool.TunnelChannelPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class CopyChannelHandler extends SimpleChannelInboundHandler<CopyRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CopyRequestMessage msg) throws Exception {
        String tunnelConnId = ctx.channel().id().asLongText();
        String key = tunnelConnId + "@" + msg.getConnId();
        Channel channel = ChannelPool.getChannel(key);
        ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(msg.getData());
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(byteBuf);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        TunnelChannelPool.remove(ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        TunnelChannelPool.remove(ctx.channel().id().asLongText());
        super.exceptionCaught(ctx, cause);
        log.error("err", cause);
    }
}
