package com.candy.handler;

import com.candy.message.CloseChannelRequestMessage;
import com.candy.message.CopyRequestMessage;
import com.candy.pool.ChannelPool;
import com.candy.tunnel.TunnelClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class TcpTransportChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接断开 remote addr :{}", ctx.channel().remoteAddress());
        CloseChannelRequestMessage closeMessage = new CloseChannelRequestMessage();
        closeMessage.setConnId(ctx.channel().id().asLongText());
        TunnelClient.getChannel().writeAndFlush(closeMessage);
        ChannelPool.remove(ctx.channel().id().asLongText());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        CopyRequestMessage copyRequestMessage = new CopyRequestMessage();
        copyRequestMessage.setConnId(ctx.channel().id().asLongText());
        int length = buf.readableBytes();
        byte[] data = new byte[length];
        buf.readBytes(data);
        ReferenceCountUtil.release(buf);
        copyRequestMessage.setData(data);
        TunnelClient.getChannel().writeAndFlush(copyRequestMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("e", cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
